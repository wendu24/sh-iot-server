package com.ruoyi.business.iot.observer;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.service.MsgSetReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class PublishMsgObserver implements MqttMsgObserver {

    @Autowired
    MsgSetReplyService msgSetReplyService;

    @Override
    public void handle(String topic, IotMsg iotMsg) {
        DtuDownDataVO dtuDownDataVO = (DtuDownDataVO) iotMsg;
        ArrayList<MsgSetReplyDO> msgSetReplyList = new ArrayList<>();
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> {
            MsgSetReplyDO msgSetReplyDO = new MsgSetReplyDO();
            msgSetReplyDO.setDeviceSn(commonDownDataVO.getDeviceSn());
            msgSetReplyDO.setCmdCode(((Byte)commonDownDataVO.getCmdCode()).intValue());
            msgSetReplyDO.setMid(commonDownDataVO.getMid().intValue());
            msgSetReplyDO.setTopic(topic);
            msgSetReplyDO.setPublishTime(dtuDownDataVO.getPublishTime());
            msgSetReplyDO.setMsgBody(JSONObject.toJSONString(commonDownDataVO));
            msgSetReplyDO.setCreateTime(LocalDateTime.now());
            msgSetReplyList.add(msgSetReplyDO);
        });
        msgSetReplyService.saveBatch(msgSetReplyList);
    }

    @Override
    @PostConstruct
    public void register() {
        MqttMsgProducer.addHandler(TopicConstant.PUBLISH_SET,this);
    }
}
