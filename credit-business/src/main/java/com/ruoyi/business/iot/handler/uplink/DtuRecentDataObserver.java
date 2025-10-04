package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MqttDeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MqttDeviceLatestDataService;
import com.ruoyi.business.service.MqttDeviceRecentDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 更新dtu设备的数据
 */
@Component
@Slf4j
public class DtuRecentDataObserver extends AbstractUplinkMsgObserver {

    @Autowired
    private MqttDeviceRecentDataService mqttDeviceRecentDataService;

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

        MqttDeviceRecentDataDO mqttDeviceRecentDataDO = new MqttDeviceRecentDataDO();
        mqttDeviceRecentDataDO.setDeviceSn(dtuDataVO.getDtuDeviceSn());
        mqttDeviceRecentDataDO.setBatteryLevel(dtuDataVO.getBatteryLevel());
        mqttDeviceRecentDataDO.setSignalStrength(dtuDataVO.getSignalStrength());
        mqttDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
        mqttDeviceRecentDataDO.setCommunityName(deviceDO.getCommunityName());
        mqttDeviceRecentDataDO.setCommunityId(deviceDO.getCommunityId());
        mqttDeviceRecentDataDO.setIccId(dtuDataVO.getIccId());
        mqttDeviceRecentDataService.save(mqttDeviceRecentDataDO);
        log.info("保存dtu数据成功");
    }
}
