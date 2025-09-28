package com.ruoyi.business.iot.observer;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.iot.common.vo.IotMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MqttMsgProducer {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    private static final Map<String , List<MqttMsgObserver>> handlerMap = new ConcurrentHashMap<>() ;

    public void handle(String topic, IotMsg iotMsg){
        mqttMessageExecutor.execute(()->{
            String[] parts = topic.split("/");
            String topicSuffer = parts[4];
            handlerMap.get(topicSuffer).forEach(mqttMsgObserver -> mqttMsgObserver.handle(topic,iotMsg));
        });
    }

    public static void addHandler(String topicSuffix, MqttMsgObserver mqttMsgObserver){
        handlerMap.computeIfAbsent(topicSuffix, k -> new ArrayList<>())
                .add(mqttMsgObserver);
    }

}
