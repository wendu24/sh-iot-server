package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MqttRecentDataObserver extends AbstractUplinkMsgObserver {

// MqttHistoryDataObserver
    @Autowired
    private MqttDeviceRecentDataService mqttDeviceRecentDataService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

        DtuDataVO dtuDataVO = uplinkDataVO.getDtuDataVO();
        if (Objects.isNull(dtuDataVO))
            return;
        MqttDeviceRecentDataDO mqttDeviceRecentDataDO = new MqttDeviceRecentDataDO();
        mqttDeviceRecentDataDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceRecentDataDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceRecentDataDO.setSignalStrength(dtuDataVO.getSignalStrength());
        mqttDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
        mqttDeviceRecentDataDO.setIccId(dtuDataVO.getIccId());
        mqttDeviceRecentDataService.save(mqttDeviceRecentDataDO);

    }
}
