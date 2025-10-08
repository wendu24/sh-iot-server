package com.ruoyi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.mapper.MqttDeviceLatestDataMapper;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import com.ruoyi.business.vo.DtuLatestDataVO;
import com.ruoyi.business.vo.MqttLatestDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class MqttDeviceLatestDataServiceImpl extends ServiceImpl<MqttDeviceLatestDataMapper, MqttDeviceLatestDataDO> implements MqttDeviceLatestDataService {


    @Override
    public DtuLatestDataVO findDtuByDeviceSN(String deviceSn){

        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MqttDeviceLatestDataDO::getDeviceSn,deviceSn);
        MqttDeviceLatestDataDO dtuLatestData = getOne(queryWrapper);
        DtuLatestDataVO dtuLatestDataVO = DtuLatestDataVO.builder().build();

        if(Objects.isNull(dtuLatestData))
            return dtuLatestDataVO;

        BeanUtil.copyProperties(dtuLatestData,dtuLatestDataVO);
        return dtuLatestDataVO;
    }


    @Override
    public MqttLatestDataVO findMqttByDeviceSN(String deviceSn){

        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MqttDeviceLatestDataDO::getDeviceSn,deviceSn);
        MqttDeviceLatestDataDO mqttLatestData = getOne(queryWrapper);
        MqttLatestDataVO mqttLatestDataVO = MqttLatestDataVO.builder().build();

        if(Objects.isNull(mqttLatestData))
            return mqttLatestDataVO;

        BeanUtil.copyProperties(mqttLatestData,mqttLatestDataVO);
        return mqttLatestDataVO;
    }



}
