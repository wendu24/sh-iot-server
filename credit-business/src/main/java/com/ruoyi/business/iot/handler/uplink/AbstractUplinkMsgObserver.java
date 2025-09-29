package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.iot.handler.UplinkMsgHandler;

import javax.annotation.PostConstruct;

public abstract class AbstractUplinkMsgObserver implements UplinkMsgObserver{

    @PostConstruct
    public void register(){
        UplinkMsgHandler.addHandler(this);
    }
}
