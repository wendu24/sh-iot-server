package com.ruoyi.business.iot;

import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.observer.MqttMsgProducer;
import com.ruoyi.business.iot.packager.MqttDataPackager;
import com.ruoyi.business.iot.parser.MqttDataParser;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * 发布和订阅消息
 */
@Component
@Slf4j
public class MqttService {

    @Autowired
    private MqttClient mqttClient;


    @Autowired
    MqttMsgProducer mqttMsgProducer;

    @Autowired
    MidGenerator midGenerator;




    public void publish(String topicDeviceSn, DtuDownDataVO dtuDownDataVO) throws Exception{
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
        dtuDownDataVO.setPublishTime(LocalDateTime.now());
        byte[] dataBytes = MqttDataPackager.build(dtuDownDataVO, AesUtil.getAesKey(topicDeviceSn));
        String topic = "tje/unit/cmd/"+topicDeviceSn+"/set";
        int qos = 0;
        publish(topic,dataBytes,qos);
        mqttMsgProducer.handle(topic,dtuDownDataVO);
    }

    // 发布消息
    private void publish(String topic, byte[] payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        mqttClient.publish(topic, message);
        System.out.println("Published to topic: " + topic + ", payload: " + payload);
    }



    @PostConstruct
    public void subscribeIotDataTopics() throws MqttException {
        String[] topics = {"tje/unit/evt/+/data"};
        int[] qos = {0};

        IMqttMessageListener[] listeners = new IMqttMessageListener[topics.length];
        for (int i = 0; i < topics.length; i++) {
            listeners[i] = (topic, message) -> {
                log.info("收到消息 Topic={} ,msg={}",topic,IotCommonUtil.bytesToHex(message.getPayload()));
                DtuDataVO dtuDataVO = MqttDataParser.parse(topic, IotCommonUtil.bytesToHex(message.getPayload()));
                mqttMsgProducer.handle(topic,dtuDataVO);
            };
        }
        mqttClient.subscribe(topics, qos, listeners);
    }

    @PostConstruct
    public void subscribeIotReplyTopics() throws MqttException {
        String[] topics = { "tje/unit/cmd/+/set_reply"};
        int[] qos = {0};

        // 定义统一的消息监听器
        IMqttMessageListener[] listeners = new IMqttMessageListener[topics.length];
        for (int i = 0; i < topics.length; i++) {
            listeners[i] = (topic, message) -> {
                log.info("收到消息 Topic={} ,msg={}",topic,IotCommonUtil.bytesToHex(message.getPayload()));
                DtuDataVO dtuDataVO = MqttDataParser.parse(topic, IotCommonUtil.bytesToHex(message.getPayload()));
                mqttMsgProducer.handle(topic,dtuDataVO);
            };
        }
        mqttClient.subscribe(topics, qos, listeners);
    }
}
