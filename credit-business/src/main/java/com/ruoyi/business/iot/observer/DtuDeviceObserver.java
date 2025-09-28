package com.ruoyi.business.iot.observer;

import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 更新dtu设备的数据
 */
@Component
public class DtuDeviceObserver implements MqttMsgObserver{

    @Override
    public void handle(String topic, IotMsg iotMsg) {

    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgProducer.addHandler(TopicConstant.UNIT_DATA,this);
    }
}
