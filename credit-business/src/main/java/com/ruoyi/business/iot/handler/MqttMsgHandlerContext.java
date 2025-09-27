package com.ruoyi.business.iot.handler;

import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqttMsgHandlerContext {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    private static final Map<String , MqttMsgHandler> handlerMap = new ConcurrentHashMap<>() ;

    public void handle(String topic, DtuDataVO dtuDataVO){
        mqttMessageExecutor.execute(()->{
            handlerMap.get(topic).handle(dtuDataVO);
        });
    }

    public static void addHandler(String topic, MqttMsgHandler mqttMsgHandler){
        handlerMap.put(topic,mqttMsgHandler);
    }

}
