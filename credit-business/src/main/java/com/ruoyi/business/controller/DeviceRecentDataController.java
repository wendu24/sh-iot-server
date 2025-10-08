package com.ruoyi.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.domain.UdpDeviceRecentDataDO;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import com.ruoyi.business.service.UdpDeviceRecentDataService;
import com.ruoyi.business.vo.RecentDataQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/data/recent")
public class DeviceRecentDataController {


    @Autowired
    UdpDeviceRecentDataService udpDeviceRecentDataService;

    @Autowired
    MqttDeviceRecentDataService mqttDeviceRecentDataService;

    @RequestMapping("/udp/list")
    public Page<UdpDeviceRecentDataDO> udpList(@RequestBody RecentDataQueryVO recentDataQueryVO){
        return udpDeviceRecentDataService.list(recentDataQueryVO);
    }

    @RequestMapping("/mqtt/list")
    public Page<MqttDeviceRecentDataDO> mqttList(@RequestBody RecentDataQueryVO recentDataQueryVO){
        return mqttDeviceRecentDataService.list(recentDataQueryVO);
    }
}
