package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.handler.uplink.UplinkMsgObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class UplinkMsgHandler {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    private static final List<UplinkMsgObserver> observers = new CopyOnWriteArrayList<>() ;

    public void handle( UplinkDataVO uplinkDataVO){
        mqttMessageExecutor.execute(()->{
            observers.forEach(mqttMsgObserver -> mqttMsgObserver.handle( uplinkDataVO));
        });
    }


    public static void addHandler(UplinkMsgObserver uplinkMsgObserver){
        observers.add(uplinkMsgObserver);
    }

}
