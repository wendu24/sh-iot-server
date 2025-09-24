package com.ruoyi.business.iot;

import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.subscribe.MqttMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MqttService {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private MqttMsgHandler mqttMsgHandler;

    // 发布消息
    public void publish(String topic, String payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        mqttClient.publish(topic, message);
        System.out.println("Published to topic: " + topic + ", payload: " + payload);
    }

    // 订阅多个消息
    @PostConstruct
    public void subscribeMultipleTopics() throws MqttException {
        String[] topics = {"tje/unit/evt/+/data", "tje/unit/cmd/+/set_reply"};
        int[] qos = {0, 0}; // 为每个 topic 指定 QoS

        // 定义统一的消息监听器
        IMqttMessageListener[] listeners = new IMqttMessageListener[topics.length];
        for (int i = 0; i < topics.length; i++) {
            listeners[i] = (topic, message) -> {
                log.info("收到消息 Topic={} ,msg={}",topic,IotCommonUtil.bytesToHex(message.getPayload()));
                mqttMsgHandler.handleSync(topic,IotCommonUtil.bytesToHex(message.getPayload()));
            };
        }
        // 一次性订阅所有 Topic
        mqttClient.subscribe(topics, qos, listeners);
    }
}
