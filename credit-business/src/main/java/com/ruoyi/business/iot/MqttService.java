package com.ruoyi.business.iot;

import com.ruoyi.business.iot.common.AesUtil;
import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.publish.CompleteDataPackager;
import com.ruoyi.business.iot.publish.MqttPublisher;
import com.ruoyi.business.iot.subscribe.MqttMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 发布和订阅消息的统一对外出口
 */
@Component
@Slf4j
public class MqttService {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private MqttMsgHandler mqttMsgHandler;


    public void publish(String topicDeviceSn, DtuDownDataVO dtuDownDataVO) throws Exception{
        byte[] dataBytes = CompleteDataPackager.build(dtuDownDataVO, AesUtil.getAesKey(topicDeviceSn));
        String topic = "tje/unit/cmd/"+topicDeviceSn+"/set";
        int qos = 0;
        publish(topic,dataBytes,qos);
    }

    // 发布消息
    private void publish(String topic, byte[] payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
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
