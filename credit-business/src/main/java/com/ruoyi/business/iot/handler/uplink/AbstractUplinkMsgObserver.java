package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractUplinkMsgObserver implements UplinkMsgObserver{


    @PostConstruct
    public void register(){
        UplinkMsgHandler.addHandler(this);
    }
}
