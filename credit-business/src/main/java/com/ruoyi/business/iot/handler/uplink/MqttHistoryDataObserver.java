package com.ruoyi.business.iot.handler.uplink;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.MqttDeviceDataTemplateDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.service.MqttDeviceDataTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将消息存入 recent表
 */
@Slf4j
@Component
public class MqttHistoryDataObserver extends AbstractUplinkMsgObserver {

    @Autowired
    MqttDeviceDataTemplateService mqttDeviceDataTemplateService;

    @Override
    public void handle( UplinkDataVO uplinkDataVO) {
        List<MqttCmd08DataVO> mqttCmd08DataVOS = uplinkDataVO.getMqttCmd08DataVOS();
        if(CollectionUtils.isEmpty(mqttCmd08DataVOS))
            return;
        List<MqttDeviceDataTemplateDO> addList = new ArrayList<>();

        uplinkDataVO.getMqttCmd08DataVOS().forEach(uplinkCmd08DataVO -> {
            MqttDeviceDataTemplateDO mqttDeviceRecentDataDO = new MqttDeviceDataTemplateDO();
            BeanUtil.copyProperties(uplinkCmd08DataVO, mqttDeviceRecentDataDO);
            String abnormalTypes = uplinkCmd08DataVO.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                mqttDeviceRecentDataDO.setAbnormalTypes(abnormalTypes);
            mqttDeviceRecentDataDO.setUplinkPeriod(uplinkCmd08DataVO.getUplinkPeriod().intValue());
            mqttDeviceRecentDataDO.setCreateTime(LocalDateTime.now());
            addList.add(mqttDeviceRecentDataDO);
        });
        mqttDeviceDataTemplateService.saveBatch(addList);

    }

}
