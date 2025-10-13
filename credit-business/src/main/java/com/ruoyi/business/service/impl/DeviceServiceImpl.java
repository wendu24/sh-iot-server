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
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.vo.DeviceVO;
import com.ruoyi.business.vo.RefreshDeviceVO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceDO> implements DeviceService {


    @Autowired
    MqttService mqttService;

    @Autowired
    UdpService udpService;


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
        refreshData(deviceVO, true);
    }


    @Override
    public void refreshData(RefreshDeviceVO refreshDeviceVO, boolean usingCache) {
        findByDeviceSn2(refreshDeviceVO.getDeviceSnList()).forEach(deviceDO -> {
            log.info("开始刷新设备的数据sn{}", deviceDO.getDeviceSn());
            DeviceVO deviceVO = DeviceVO.builder().build();
            BeanUtil.copyProperties(deviceDO, deviceVO);
            refreshData(deviceVO, usingCache);
        });

    }


    private void refreshData(DeviceVO deviceVO, boolean usingCache) {
        List<CommonDownDataVO> commonDownDataVOS = DownCmdEnum.autoFreshCommands()
                .stream()
                .map(cmdEnum -> CommonDownDataVO.builder()
                        .deviceSn(deviceVO.getDeviceSn())
                        .readWriteFlag(ReadWriteEnum.READ.getCode())
                        .cmdCode(cmdEnum.getCode())
                        .build())
                .collect(Collectors.toList());

        /**
         * 单条命令下发,避免设备不支持批量
         */
        commonDownDataVOS.forEach(commonDownDataVO -> {
            DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build();
            try {
                if (deviceVO.getDeviceType().equals(DeviceTypeEnum.DEV_TEMPERATURE.getCode())) {
                    if (usingCache) {
                        udpService.sendCommand2cache(deviceVO.getDeviceSn(), dtuDownDataVO);
                    } else {
                        udpService.sendCommandAsync(deviceVO.getDeviceSn(), dtuDownDataVO);
                    }
                } else {
                    mqttService.publish(deviceVO.getDtuSn(), dtuDownDataVO);
                }
            } catch (Exception e) {
                log.error("新增设备时,刷新数据出错sn={}", deviceVO.getDeviceSn(), e);
            }

        });
    }


    @Override
    public void update(DeviceVO deviceVO) {
        DeviceDO updateData = new DeviceDO();
        BeanUtil.copyProperties(deviceVO, updateData);
        updateData.setUpdateTime(LocalDateTime.now());
        updateById(updateData);
    }

    /**
     * 下发命令读取设备参数
     */
    @Override
    public void publishMsg(List<CommonDownDataVO> commonDownDataVOS, boolean usingCache) {


        List<String> deviceSnList = commonDownDataVOS.stream().map(CommonDownDataVO::getDeviceSn).distinct().collect(Collectors.toList());
        Map<String, CommonDownDataVO> downDataVOMap = commonDownDataVOS.stream().collect(Collectors.toMap(CommonDownDataVO::getDeviceSn, Function.identity(), (t1, t2) -> t1));

        List<DeviceDO> deviceDOList = findByDeviceSn2(deviceSnList);
        Map<String, DeviceDO> dtuMap = deviceDOList.stream()
                .filter(deviceDO -> StringUtils.isNotBlank(deviceDO.getDtuSn()))
                .collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));


        /**
         * 发布mqtt消息
         */
        dtuMap.forEach((sn, deviceDO) -> {
            CommonDownDataVO commonDownDataVO = downDataVOMap.get(sn);

            try {
                mqttService.publish(deviceDO.getDtuSn(), DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build());
            } catch (Exception e) {
                log.error("MQTT消息发布出错啦commonDownDataVO={}", JSONObject.toJSONString(commonDownDataVO), e);
            }
        });

        /**
         * 发布udp消息
         */
        Map<String, DeviceDO> udpMap = deviceDOList.stream()
                .filter(deviceDO -> StringUtils.isEmpty(deviceDO.getDtuSn()))
                .collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(), (t1, t2) -> t1));

        udpMap.forEach((sn, deviceDO) -> {
            CommonDownDataVO commonDownDataVO = downDataVOMap.get(sn);

            try {
                if(usingCache){
                    udpService.sendCommand2cache(sn, DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build());
                }else{
                    udpService.sendCommandAsync(sn, DtuDownDataVO.builder().dataVOList(Arrays.asList(commonDownDataVO)).build());
                }
            } catch (Exception e) {
                log.error("UDP消息发布出错啦commonDownDataVO={}", JSONObject.toJSONString(commonDownDataVO), e);
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
