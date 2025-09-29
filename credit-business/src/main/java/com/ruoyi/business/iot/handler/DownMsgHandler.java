package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.handler.down.DownMsgObserver;
import com.ruoyi.business.iot.handler.uplink.UplinkMsgObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class DownMsgHandler {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    private static final List<DownMsgObserver> observers = new CopyOnWriteArrayList<>() ;

    public void handle( DtuDownDataVO dtuDownDataVO){
        mqttMessageExecutor.execute(()->{
            observers.forEach(mqttMsgObserver -> mqttMsgObserver.handle( dtuDownDataVO));
        });
    }


    public static void addHandler(DownMsgObserver downMsgObserver){
        observers.add(downMsgObserver);
    }

}
