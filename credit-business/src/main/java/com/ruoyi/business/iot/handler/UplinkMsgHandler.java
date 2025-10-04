package com.ruoyi.business.iot.handler;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.handler.uplink.UplinkMsgObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class UplinkMsgHandler {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    private static final List<UplinkMsgObserver> observers = new CopyOnWriteArrayList<>() ;

    public void handle( UplinkDataVO uplinkDataVO){
        mqttMessageExecutor.execute(()->{
            try {
                observers.forEach(mqttMsgObserver -> mqttMsgObserver.handle( uplinkDataVO));
            } catch (Exception e) {
                log.error("数据处理出错了uplinkDataVO={}", JSONObject.toJSONString(uplinkDataVO),e);
            }
        });
    }


    public static void addHandler(UplinkMsgObserver uplinkMsgObserver){
        observers.add(uplinkMsgObserver);
    }

}
