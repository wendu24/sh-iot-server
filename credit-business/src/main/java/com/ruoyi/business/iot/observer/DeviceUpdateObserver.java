package com.ruoyi.business.iot.observer;

import com.ruoyi.business.iot.common.vo.IotMsg;
import org.springframework.stereotype.Component;

/**
 * 将回复消息或者上传的数据更新到设备表
 */
@Component
public class DeviceUpdateObserver implements MqttMsgObserver{
    @Override
    public void handle(String topic, IotMsg iotMsg) {

    }

    @Override
    public void register() {

    }
}
