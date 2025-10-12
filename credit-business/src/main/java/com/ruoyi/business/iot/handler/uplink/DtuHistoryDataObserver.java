package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MqttDeviceDataTemplateDO;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MqttDeviceDataTemplateService;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;


@Component
@Slf4j
public class DtuHistoryDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private MqttDeviceDataTemplateService mqttDeviceDataTemplateService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

        DtuDataVO dtuDataVO = uplinkDataVO.getDtuDataVO();
        if (Objects.isNull(dtuDataVO))
            return;

        String dtuDeviceSn = uplinkDataVO.getDtuDataVO().getDtuDeviceSn();
        DeviceDO deviceDO;
        try {
            deviceDO = deviceService.findByDeviceSn(dtuDeviceSn);
        } catch (Exception e) {
            log.error("未找到deviceSn={]",dtuDeviceSn,e);
            return;
        }
        MqttDeviceDataTemplateDO mqttDeviceDataTemplateDO = new MqttDeviceDataTemplateDO();
        mqttDeviceDataTemplateDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceDataTemplateDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceDataTemplateDO.setSignalStrength(dtuDataVO.getSignalStrength());
        mqttDeviceDataTemplateDO.setCreateTime(LocalDateTime.now());
        mqttDeviceDataTemplateDO.setCommunityName(deviceDO.getCommunityName());
        mqttDeviceDataTemplateDO.setCommunityId(deviceDO.getCommunityId());
        mqttDeviceDataTemplateDO.setIccId(dtuDataVO.getIccId());
        log.info("收到dtu设备上报数据,保存到历史数据表 mqttDeviceDataTemplateDO={}", mqttDeviceDataTemplateDO.getDeviceSn());
        mqttDeviceDataTemplateService.save(mqttDeviceDataTemplateDO);

    }
}
