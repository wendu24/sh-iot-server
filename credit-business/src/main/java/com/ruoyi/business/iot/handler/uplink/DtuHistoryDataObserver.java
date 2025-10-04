package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.domain.MqttDeviceDataTemplateDO;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.MqttDeviceDataTemplateService;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;


@Component
public class DtuHistoryDataObserver extends AbstractUplinkMsgObserver{
    @Autowired
    private MqttDeviceDataTemplateService mqttDeviceDataTemplateService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

        DtuDataVO dtuDataVO = uplinkDataVO.getDtuDataVO();
        if (Objects.isNull(dtuDataVO))
            return;
        MqttDeviceDataTemplateDO mqttDeviceDataTemplateDO = new MqttDeviceDataTemplateDO();
        mqttDeviceDataTemplateDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceDataTemplateDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceDataTemplateDO.setSignalStrength(dtuDataVO.getSignalStrength());
        mqttDeviceDataTemplateDO.setCreateTime(LocalDateTime.now());
        mqttDeviceDataTemplateDO.setIccId(dtuDataVO.getIccId());
        mqttDeviceDataTemplateService.save(mqttDeviceDataTemplateDO);

    }
}
