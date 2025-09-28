package com.ruoyi.business.iot.observer;

import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataMsgObserver implements MqttMsgObserver {
    @Override
    public void handle(String topic, IotMsg dtuDataVO) {

    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgProducer.addHandler(TopicConstant.UNIT_DATA,this);
    }
}
