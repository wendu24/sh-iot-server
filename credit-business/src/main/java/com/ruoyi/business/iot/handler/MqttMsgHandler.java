package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;

public interface MqttMsgHandler {

    public void handle(String topic, IotMsg iotMsg);


    public void register();

}
