package com.ruoyi.business.iot.observer;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.business.domain.DeviceLatestDataDO;
import com.ruoyi.business.domain.DeviceRecentDataDO;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.service.DeviceRecentDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 将消息存入 recent表
 */
@Slf4j
@Component
public class RecentDeviceDataObserver implements MqttMsgObserver{

    @Autowired
    DeviceRecentDataService deviceRecentDataService;

    @Override
    public void handle(String topic, IotMsg iotMsg) {
        DtuDataVO dtuDataVO = (DtuDataVO) iotMsg;

        List<DeviceRecentDataDO> addList = new ArrayList<>();

        dtuDataVO.getCmd08DataVOS().forEach(uplinkCmd08DataVO -> {
            DeviceRecentDataDO deviceRecentDataDO = new DeviceRecentDataDO();
            BeanUtil.copyProperties(uplinkCmd08DataVO,deviceRecentDataDO);
            String abnormalTypes = uplinkCmd08DataVO.getAbnormalTypes()
                    .stream()
                    .map(AbnormalTypeEnum::getCode)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(abnormalTypes))
                deviceRecentDataDO.setAbnormalTypes(abnormalTypes);
            deviceRecentDataDO.setUplinkPeriod(uplinkCmd08DataVO.getUplinkPeriod().intValue());
            deviceRecentDataDO.setCreateTime(LocalDateTime.now());
            addList.add(deviceRecentDataDO);
        });
        deviceRecentDataService.saveBatch(addList);

    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgProducer.addHandler(TopicConstant.UNIT_DATA,this);
    }
}
