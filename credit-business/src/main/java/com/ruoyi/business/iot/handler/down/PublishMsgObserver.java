package com.ruoyi.business.iot.handler.down;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotUplinkMsg;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.handler.uplink.UplinkMsgObserver;
import com.ruoyi.business.service.MsgSetReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class PublishMsgObserver extends AbstractDownMsgObserver {

    @Autowired
    MsgSetReplyService msgSetReplyService;

    @Override
    public void handle( DtuDownDataVO dtuDownDataVO) {
        ArrayList<MsgSetReplyDO> msgSetReplyList = new ArrayList<>();
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> {
            if(commonDownDataVO.getCmdCode() == CmdEnum.DOWNLINK_FF.getCode())
                return;
            MsgSetReplyDO msgSetReplyDO = new MsgSetReplyDO();
            msgSetReplyDO.setDeviceSn(commonDownDataVO.getDeviceSn());
            msgSetReplyDO.setCmdCode(((Byte)commonDownDataVO.getCmdCode()).intValue());
            msgSetReplyDO.setMid(commonDownDataVO.getMid().intValue());
//            msgSetReplyDO.setTopic(topic);
            msgSetReplyDO.setPublishTime(dtuDownDataVO.getPublishTime());
            msgSetReplyDO.setMsgBody(JSONObject.toJSONString(commonDownDataVO));
            msgSetReplyDO.setCreateTime(LocalDateTime.now());
            msgSetReplyList.add(msgSetReplyDO);
        });
        if(CollectionUtils.isEmpty(msgSetReplyList))
            return;
        msgSetReplyService.saveBatch(msgSetReplyList);
    }

}
