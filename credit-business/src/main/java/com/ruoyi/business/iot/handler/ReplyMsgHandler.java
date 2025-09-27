package com.ruoyi.business.iot.handler;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.TopicConstant;
import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import com.ruoyi.business.service.MsgSetReplyService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReplyMsgHandler implements MqttMsgHandler{

    @Autowired
    MsgSetReplyService msgSetReplyService;

    @Override
    public void handle(String topic, IotMsg iotMsg) {
        DtuDataVO dtuDataVO = (DtuDataVO) iotMsg;
        List<UplinkCmdFFDataVO> cmdFFDataVOS = dtuDataVO.getCmdFFDataVOS();
        Map<String, MsgSetReplyDO> dbPublishMsgMap = findDbPublishMsggMap(cmdFFDataVOS);
        List<UplinkCmdFFDataVO> validMsgs = filterByDbMsg(cmdFFDataVOS, dbPublishMsgMap, dtuDataVO);
        if(CollectionUtils.isEmpty(validMsgs))
            return;
        updateReplyInfo(cmdFFDataVOS, dbPublishMsgMap);

    }

    private void updateReplyInfo(List<UplinkCmdFFDataVO> cmdFFDataVOS, Map<String, MsgSetReplyDO> dbPublishMsgMap) {
        List<MsgSetReplyDO> replyMsgDates = cmdFFDataVOS.stream().map(cmdFFDataVO -> {
            MsgSetReplyDO dbPublishMsg = dbPublishMsgMap.get(cmdFFDataVO.msgKey());
            MsgSetReplyDO replyMsg = new MsgSetReplyDO();
            replyMsg.setId(dbPublishMsg.getId());
            replyMsg.setReplyTime(LocalDateTime.now());
            replyMsg.setReplyBody(JSONObject.toJSONString(cmdFFDataVO));
            return replyMsg;
        }).collect(Collectors.toList());
        msgSetReplyService.updateBatchById(replyMsgDates);
    }

    private static List<UplinkCmdFFDataVO> filterByDbMsg(List<UplinkCmdFFDataVO> cmdFFDataVOS, Map<String, MsgSetReplyDO> dbPublishMsgMap, DtuDataVO dtuDataVO) {
        List<UplinkCmdFFDataVO> validMsgs = cmdFFDataVOS.stream().filter(cmdFFDataVO -> {
            MsgSetReplyDO dbPublishMsg = dbPublishMsgMap.get(cmdFFDataVO.msgKey());
            if (Objects.isNull(dbPublishMsg)) {
                log.error("超时消息{}", JSONObject.toJSONString(dtuDataVO));
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return validMsgs;
    }

    private @NotNull Map<String, MsgSetReplyDO> findDbPublishMsggMap(List<UplinkCmdFFDataVO> cmdFFDataVOS) {
        List<Short> midList = cmdFFDataVOS.stream().map(UplinkCmdFFDataVO::getMid).collect(Collectors.toList());
        LambdaQueryWrapper<MsgSetReplyDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MsgSetReplyDO::getMid,midList);
        queryWrapper.isNull(MsgSetReplyDO::getReplyTime);
        queryWrapper.isNull(MsgSetReplyDO::getReplyBody);
        queryWrapper.ge(MsgSetReplyDO::getPublishTime, LocalDateTime.now().plusHours(-5));
        queryWrapper.select(MsgSetReplyDO::getId,MsgSetReplyDO::getMid,MsgSetReplyDO::getDeviceSn);
        Map<String, MsgSetReplyDO> dbPublishMsgMap = msgSetReplyService.list(queryWrapper)
                .stream().collect(Collectors.toMap(MsgSetReplyDO::msgKey, Function.identity(), (t1, t2) -> t1));
        return dbPublishMsgMap;
    }


    @Override
    @PostConstruct
    public void register() {
        MqttMsgHandlerContext.addHandler(TopicConstant.UNIT_SET_REPLY,this);
    }
}
