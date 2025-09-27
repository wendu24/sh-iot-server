package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataMsgHandler implements MqttMsgHandler{
    @Override
    public void handle(DtuDataVO dtuDataVO) {

    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgHandlerContext.addHandler(TopicConstant.UNIT_DATA,this);
    }
}
