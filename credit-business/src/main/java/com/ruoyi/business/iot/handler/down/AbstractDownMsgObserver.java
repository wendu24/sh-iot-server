package com.ruoyi.business.iot.handler.down;

import com.ruoyi.business.iot.handler.DownMsgHandler;

import javax.annotation.PostConstruct;

public abstract class AbstractDownMsgObserver implements DownMsgObserver{

    @PostConstruct
    public void register(){
        DownMsgHandler.addHandler(this);
    }
}
