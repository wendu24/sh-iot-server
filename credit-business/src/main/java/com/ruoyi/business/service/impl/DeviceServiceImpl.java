package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.constant.DeviceTypeEnum;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.MqttService;
import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MsgSetReplyService;
import com.ruoyi.business.util.RedisKeyUtil;
import com.ruoyi.business.vo.DeviceVO;
import com.ruoyi.business.vo.RefreshDeviceVO;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceDO> implements DeviceService {


    @Autowired
    MqttService mqttService;

    @Autowired
    UdpService udpService;

    @Autowired
    RedisCache redisCache;

    @Autowired
    MidGenerator midGenerator;

    @Autowired
    MsgSetReplyService msgSetReplyService;


    @Override
    public Page<DeviceDO> list(DeviceVO deviceVO) {

        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(deviceVO.getDeviceSn()), DeviceDO::getDeviceSn, deviceVO.getDeviceSn());
        queryWrapper.eq(StringUtils.isNotEmpty(deviceVO.getDtuSn()), DeviceDO::getDtuSn, deviceVO.getDtuSn());
        queryWrapper.eq(Objects.nonNull(deviceVO.getDeviceType()), DeviceDO::getDeviceType, deviceVO.getDeviceType());
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        queryWrapper.like(StringUtils.isNotEmpty(deviceVO.getCommunityName()), DeviceDO::getCommunityName, deviceVO.getCommunityName());
        queryWrapper.orderByDesc(DeviceDO::getId);
        Page<DeviceDO> pageParam = new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize());
        return page(pageParam, queryWrapper);
    }


    @Override
    public void add(DeviceVO deviceVO) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn, deviceVO.getDeviceSn());
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO dbData = getOne(queryWrapper);
        if (Objects.nonNull(dbData))
            throw new ServiceException("sn 已存在");
        DeviceDO newData = new DeviceDO();
        BeanUtil.copyProperties(deviceVO, newData);
        newData.setCreateTime(LocalDateTime.now());
        newData.setUpdateTime(LocalDateTime.now());
        save(newData);

        /**
         * 刷新设备数据
         */
        refreshData(deviceVO);
    }


    @Override
    public void refreshData(RefreshDeviceVO refreshDeviceVO) {
        findByDeviceSn2(refreshDeviceVO.getDeviceSnList()).forEach(deviceDO -> {
            log.info("开始刷新设备的数据sn{}", deviceDO.getDeviceSn());
            DeviceVO deviceVO = DeviceVO.builder().build();
            BeanUtil.copyProperties(deviceDO, deviceVO);
            refreshData(deviceVO);
        });

    }


    private void refreshData(DeviceVO deviceVO) {
        if(StringUtils.isBlank(deviceVO.getDtuSn())){
            List<CommonDownDataVO> commonDownDataVOS = DownCmdEnum.udpAutoFreshCommands()
                    .stream()
                    .map(cmdEnum -> CommonDownDataVO.builder()
                            .deviceSn(deviceVO.getDeviceSn())
                            .readWriteFlag(ReadWriteEnum.READ.getCode())
                            .cmdCode(cmdEnum.getCode())
                            .build())
                    .collect(Collectors.toList());

            publishUdpMsg(commonDownDataVOS);
        }else{
            List<CommonDownDataVO> commonDownDataVOS = DownCmdEnum.mqttAutoFreshCommands()
                    .stream()
                    .map(cmdEnum -> CommonDownDataVO.builder()
                            .deviceSn(deviceVO.getDeviceSn())
                            .readWriteFlag(ReadWriteEnum.READ.getCode())
                            .cmdCode(cmdEnum.getCode())
                            .build())
                    .collect(Collectors.toList());
            publishMqttMsg(commonDownDataVOS);
        }
    }


    @Override
    public void update(DeviceVO deviceVO) {
        DeviceDO updateData = new DeviceDO();
        BeanUtil.copyProperties(deviceVO, updateData);
        updateData.setUpdateTime(LocalDateTime.now());
        updateById(updateData);
    }

    @Override
    public void publishMqttMsg(List<CommonDownDataVO> commonDownDataVOS) {
        /**
         * 查询设备信息
         */
        List<String> deviceSnList = commonDownDataVOS.stream().map(CommonDownDataVO::getDeviceSn).distinct().collect(Collectors.toList());
        List<DeviceDO> deviceDOList = findByDeviceSn2(deviceSnList);
        Map<String, DeviceDO> mattDeviceMap = deviceDOList.stream()
                .filter(deviceDO -> StringUtils.isNotBlank(deviceDO.getDtuSn()))
                .collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));
        /**
         * 发布mqtt消息
         */
        commonDownDataVOS.forEach(commonDownDataVO -> {
            DeviceDO deviceDO = mattDeviceMap.get(commonDownDataVO.getDeviceSn());
            if(Objects.isNull(deviceDO))
                return;
            try {
                mqttService.publish(deviceDO.getDtuSn(), DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build());
            } catch (Exception e) {
                log.error("MQTT消息发布出错啦commonDownDataVO={}", JSONObject.toJSONString(commonDownDataVO), e);
            }
        });
    }


    @Override
    public void publishUdpMsg(List<CommonDownDataVO> commonDownDataVOS) {

        commonDownDataVOS.forEach(commonDownDataVO -> {
            try {
                DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build();
                dtuDownDataVO.setPublishTime(LocalDateTime.now());
                commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn()));

                log.info("UDP命令加入redis 缓存={}", JSONObject.toJSONString(dtuDownDataVO));

                String udpMsgCacheKey = RedisKeyUtil.udpMsgCacheKey(commonDownDataVO.getDeviceSn());
                redisCache.setCacheMapValue(udpMsgCacheKey,String.valueOf(commonDownDataVO.getCmdCode()),dtuDownDataVO);
                redisCache.expire(udpMsgCacheKey,1L, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("UDP消息写入缓存出错啦commonDownDataVO={}", JSONObject.toJSONString(commonDownDataVO), e);
            }
        });
    }


    @Override
    public DeviceDO findByDeviceSn(String deviceSn) throws Exception {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn, deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = this.getOne(queryWrapper);
        if (Objects.isNull(deviceDO))
            throw new Exception("设备未找到" + deviceSn);
        return deviceDO;
    }

    @Override
    public Map<String, DeviceDO> findByDeviceSn(List<String> deviceSns) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceDO::getDeviceSn, deviceSns);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        return list(queryWrapper).stream().collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));
    }

    @Override
    public List<DeviceDO> findByDeviceSn2(List<String> deviceSns) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceDO::getDeviceSn, deviceSns);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        return list(queryWrapper);
    }





}
