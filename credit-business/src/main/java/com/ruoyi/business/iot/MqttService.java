package com.ruoyi.business.iot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.business.constant.DeleteEnum;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.handler.DownMsgHandler;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.packager.mqtt.MqttDataPackager;
import com.ruoyi.business.iot.parser.MqttDataParseContext;
import com.ruoyi.business.mapper.DeviceMapper;
import com.ruoyi.business.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 发布和订阅消息
 */
@Component
@Slf4j
public class MqttService {

    @Autowired
    private MqttClient mqttClient;


    @Autowired
    UplinkMsgHandler uplinkMsgHandler;

    @Autowired
    DownMsgHandler downMsgHandler;

    @Autowired
    MidGenerator midGenerator;

    @Autowired
    DeviceMapper deviceMapper;

    /**
     * 发布命令,所有命令需要是同一个dtu下的
     * @param topicDeviceSn dtu sn
     * @param dtuDownDataVO
     * @throws Exception
     */
    public void publish(String topicDeviceSn, DtuDownDataVO dtuDownDataVO) throws Exception{
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
        dtuDownDataVO.setPublishTime(LocalDateTime.now());
        String aesKey = getAesKey(topicDeviceSn);
        byte[] dataBytes = MqttDataPackager.build(dtuDownDataVO, aesKey);
        String topic = "tje/unit/cmd/"+topicDeviceSn+"/set";
        int qos = 0;
        publish(topic,dataBytes,qos);
        downMsgHandler.handle(dtuDownDataVO);
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
                String[] parts = topic.split("/");
                String dtuDeviceSN = parts[3];
                String aesKey = getAesKey(dtuDeviceSN);
                log.info("收到消息 Topic={} ,msg={}",topic,IotCommonUtil.bytesToHex(message.getPayload()));
                UplinkDataVO uplinkDataVO = MqttDataParseContext.parse(dtuDeviceSN, IotCommonUtil.bytesToHex(message.getPayload()),aesKey);
                uplinkMsgHandler.handle(uplinkDataVO);
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
                String[] parts = topic.split("/");
                String dtuDeviceSN = parts[3];
                String aesKey = getAesKey(dtuDeviceSN);
                log.info("收到消息 Topic={} ,msg={}",topic,IotCommonUtil.bytesToHex(message.getPayload()));
                UplinkDataVO uplinkDataVO = MqttDataParseContext.parse(dtuDeviceSN, IotCommonUtil.bytesToHex(message.getPayload()),aesKey);
                uplinkMsgHandler.handle(uplinkDataVO);
            };
        }
        mqttClient.subscribe(topics, qos, listeners);
    }


    private String getAesKey(String deviceSn) {
        LambdaQueryWrapper<DeviceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceDO::getDeviceSn,deviceSn);
        queryWrapper.eq(DeviceDO::getDeleteFlag, DeleteEnum.NORMAL.getCode());
        DeviceDO deviceDO = deviceMapper.selectOne(queryWrapper);
        if(Objects.isNull(deviceDO))
            throw new RuntimeException("'未找到设备'" + deviceSn);
        String aesKey = deviceDO.getAesKey();
        return StringUtils.isBlank(aesKey)?AesUtil.DEFAULT_AES_KEY:aesKey;
    }


}
