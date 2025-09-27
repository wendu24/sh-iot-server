package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;

public interface MqttMsgHandler {

    public void handle(DtuDataVO dtuDataVO);


    public void register();

}
