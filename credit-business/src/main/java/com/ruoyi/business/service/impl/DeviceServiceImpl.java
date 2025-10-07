package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.BizUserDO;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.MqttService;
import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.mapper.BizUserMapper;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.service.BizUserService;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.vo.DeviceVO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Page<DeviceDO> list(DeviceVO deviceVO){

        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(deviceVO.getDeviceSn()),DeviceDO::getDeviceSn,deviceVO.getDeviceSn());
        queryWrapper.eq(StringUtils.isNotEmpty(deviceVO.getDtuSn()),DeviceDO::getDtuSn,deviceVO.getDtuSn());
        queryWrapper.eq(Objects.nonNull(deviceVO.getDeviceType()),DeviceDO::getDeviceType,deviceVO.getDeviceType());
        queryWrapper.eq(DeviceDO::getDeleteFlag,DeleteEnum.NORMAL.getCode());
        queryWrapper.like(StringUtils.isNotEmpty(deviceVO.getCommunityName()),DeviceDO::getCommunityName,deviceVO.getCommunityName());
        queryWrapper.orderByDesc(DeviceDO::getId);
        Page<DeviceDO> pageParam = new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize());
        return page(pageParam,queryWrapper);
    }


    @Override
    public void add(DeviceVO deviceVO){
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceVO.getDeviceSn());
        queryWrapper.eq(DeviceDO::getDeleteFlag,DeleteEnum.NORMAL.getCode());
        DeviceDO dbData = getOne(queryWrapper);
        if(Objects.nonNull(dbData))
            throw new ServiceException("sn 已存在");
        DeviceDO newData = new DeviceDO();
        BeanUtil.copyProperties(deviceVO,newData);
        newData.setCreateTime(LocalDateTime.now());
        newData.setUpdateTime(LocalDateTime.now());
        save(newData);
    }

    /**
     * 下发命令读取设备参数
     */
    public void readDeviceParam(List<CommonDownDataVO> commonDownDataVOS){


        commonDownDataVOS.stream().map()

        mqttService.publish();



        udpService.sendCommand2cache();
    }


    @Override
    public DeviceDO findByDeviceSn(String deviceSn) throws Exception {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = this.getOne(queryWrapper);
        if(Objects.isNull(deviceDO))
            throw new Exception("设备未找到" + deviceSn);
        return deviceDO;
    }

    @Override
    public Map<String,DeviceDO> findByDeviceSn(List<String> deviceSns)  {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceDO::getDeviceSn,deviceSns);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        return list(queryWrapper).stream().collect(Collectors.toMap(DeviceDO::getDeviceSn, Function.identity(),(t1,t2)->t1));
    }

}
