package com.ruoyi.business.iot.handler.uplink;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.domain.MqttDeviceLatestDataDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class DtuLatestDataObserver extends AbstractUplinkMsgObserver{

    @Autowired
    private MqttDeviceLatestDataService mqttDeviceLatestDataService;


    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        DtuDataVO dtuDataVO = uplinkDataVO.getDtuDataVO();
        if(Objects.isNull(dtuDataVO))
            return;

        LambdaQueryWrapper<MqttDeviceLatestDataDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MqttDeviceLatestDataDO::getDeviceSn,dtuDataVO.getDtuDeviceSn());
        MqttDeviceLatestDataDO dbData = mqttDeviceLatestDataService.getOne(queryWrapper);

        MqttDeviceLatestDataDO mqttDeviceLatestDataDO = new MqttDeviceLatestDataDO();
        mqttDeviceLatestDataDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceLatestDataDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceLatestDataDO.setSignalStrength(dtuDataVO.getSignalStrength());
        if(Objects.isNull(dbData)){
            mqttDeviceLatestDataDO.setCreateTime(LocalDateTime.now());
            mqttDeviceLatestDataService.save(mqttDeviceLatestDataDO);
        }else {
            mqttDeviceLatestDataDO.setId(dbData.getId());
            mqttDeviceLatestDataService.updateById(mqttDeviceLatestDataDO);
        }

    }
}
