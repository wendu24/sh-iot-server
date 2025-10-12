package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MqttDeviceDataTemplateDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MqttDeviceDataTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 将消息存入 recent表
 */
@Slf4j
@Component
public class MqttHistoryDataObserver extends AbstractUplinkMsgObserver {

    @Autowired
    MqttDeviceDataTemplateService mqttDeviceDataTemplateService;

    @Autowired
    DeviceService deviceService;

    @Override
    public void handle( UplinkDataVO uplinkDataVO) {
        List<MqttCmd08DataVO> mqttCmd08DataVOS = uplinkDataVO.getMqttCmd08DataVOS();
        if(CollectionUtils.isEmpty(mqttCmd08DataVOS))
            return;

        List<String> deviceSnList = mqttCmd08DataVOS.stream().map(MqttCmd08DataVO::getDeviceSn).distinct().collect(Collectors.toList());
        Map<String, DeviceDO> deviceMap = deviceService.findByDeviceSn(deviceSnList);

        List<MqttDeviceDataTemplateDO> addList = new ArrayList<>();

        uplinkDataVO.getMqttCmd08DataVOS().forEach(mqttCmd08Data -> {
            MqttDeviceDataTemplateDO mqttDeviceRecentDataDO = new MqttDeviceDataTemplateDO();
            BeanUtil.copyProperties(mqttCmd08Data, mqttDeviceRecentDataDO);
            String abnormalTypes = mqttCmd08Data.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                mqttDeviceRecentDataDO.setAbnormalTypes(abnormalTypes);
            mqttDeviceRecentDataDO.setUplinkPeriod(mqttCmd08Data.getUplinkPeriod().intValue());
            mqttDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
            DeviceDO deviceDO = deviceMap.get(mqttCmd08Data.getDeviceSn());
            if (Objects.nonNull(deviceDO)) {
                mqttDeviceRecentDataDO.setCommunityId(deviceDO.getCommunityId());
                mqttDeviceRecentDataDO.setCommunityName(deviceDO.getCommunityName());
            }
            addList.add(mqttDeviceRecentDataDO);
            log.info("收到mqtt上报数据,准备保存到历史数据表={}",mqttDeviceRecentDataDO.getDeviceSn());
        });
        mqttDeviceDataTemplateService.saveBatch(addList);
        log.info("保存mqtt模板数据成功");
    }

}
