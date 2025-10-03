package com.ruoyi.business.job;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 删除数据
 */
@Component
public class DeleteRecentTableDataJob {

    @Autowired
    MqttDeviceRecentDataService mqttDeviceRecentDataService;

    @Autowired
    UdpDeviceRecentDataService udpDeviceRecentDataService;

    @Scheduled(cron = "0 0 1 * * ? ")
    public void delete(){

        LocalDateTime thirtyBefore = LocalDateTime.now().plusDays(-30);

        LambdaQueryWrapper<MqttDeviceRecentDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(MqttDeviceRecentDataDO::getCreateTime,thirtyBefore);
        mqttDeviceRecentDataService.remove(queryWrapper);


        LambdaQueryWrapper<UdpDeviceRecentDataDO> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.lt(UdpDeviceRecentDataDO::getCreateTime,thirtyBefore);
        udpDeviceRecentDataService.remove(queryWrapper1);

    }


}
