package com.ruoyi.business.controller;

import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.domain.UdpDeviceLatestDataDO;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import com.ruoyi.business.service.UdpDeviceLatestDataService;
import com.ruoyi.business.vo.DtuLatestDataVO;
import com.ruoyi.business.vo.MqttLatestDataVO;
import com.ruoyi.business.vo.UdpLatestDataVO;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/data/latest")
public class DeviceLatestDataController {


    @Autowired
    private UdpDeviceLatestDataService udpDeviceLatestDataService;

    @Autowired
    private MqttDeviceLatestDataService mqttDeviceLatestDataService;

    @RequestMapping("/udp/{deviceSn}")
    public AjaxResult udpLatestData(@PathVariable String deviceSn){
        return AjaxResult.success(udpDeviceLatestDataService.findByDeviceSN(deviceSn));
    }

    @RequestMapping("/dtu/{deviceSn}")
    public AjaxResult dtuLatestData(@PathVariable String deviceSn){

        return AjaxResult.success(mqttDeviceLatestDataService.findDtuByDeviceSN(deviceSn));
    }

    @RequestMapping("/mqtt/{deviceSn}")
    public AjaxResult mqttLatestData(@PathVariable String deviceSn){
        return AjaxResult.success(mqttDeviceLatestDataService.findMqttByDeviceSN(deviceSn));

    }

}
