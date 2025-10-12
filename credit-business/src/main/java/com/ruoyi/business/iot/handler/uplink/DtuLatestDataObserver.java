package com.ruoyi.business.iot.handler.uplink;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class DtuLatestDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private MqttDeviceLatestDataService mqttDeviceLatestDataService;

    @Autowired
    DeviceService deviceService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        DtuDataVO dtuDataVO = uplinkDataVO.getDtuDataVO();
        if(Objects.isNull(dtuDataVO))
            return;
        String dtuDeviceSn = uplinkDataVO.getDtuDataVO().getDtuDeviceSn();
        DeviceDO deviceDO;
        try {
            deviceDO = deviceService.findByDeviceSn(dtuDeviceSn);
        } catch (Exception e) {
            log.error("未找到deviceSn={]",dtuDeviceSn,e);
            return;
        }


        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MqttDeviceLatestDataDO::getDeviceSn,dtuDataVO.getDtuDeviceSn());
        MqttDeviceLatestDataDO dbData = mqttDeviceLatestDataService.getOne(queryWrapper);

        MqttDeviceLatestDataDO mqttDeviceLatestDataDO = new MqttDeviceLatestDataDO();
        mqttDeviceLatestDataDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceLatestDataDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceLatestDataDO.setCollectionTime(LocalDateTime.now());
        mqttDeviceLatestDataDO.setSignalStrength(dtuDataVO.getSignalStrength());
        mqttDeviceLatestDataDO.setCommunityName(deviceDO.getCommunityName());
        mqttDeviceLatestDataDO.setCommunityId(deviceDO.getCommunityId());
        log.info("收到dtu上报数据,开始更新最新数据表={}",mqttDeviceLatestDataDO.getDeviceSn());
        if(Objects.isNull(dbData)){
            mqttDeviceLatestDataDO.setCreateTime(LocalDateTime.now());
            mqttDeviceLatestDataService.save(mqttDeviceLatestDataDO);
        }else {
            mqttDeviceLatestDataDO.setId(dbData.getId());
            mqttDeviceLatestDataService.updateById(mqttDeviceLatestDataDO);
        }

    }
}
