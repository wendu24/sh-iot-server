package com.ruoyi.business.iot.observer;

import com.ruoyi.business.iot.common.vo.IotMsg;

public interface MqttMsgObserver {

    public void handle(String topic, IotMsg iotMsg);


    public void register();

}
