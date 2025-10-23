package com.ruoyi.business.iot.handler.uplink;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.service.MsgSetReplyService;
import com.ruoyi.business.util.RedisKeyUtil;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReplyMsgObserver extends AbstractUplinkMsgObserver {

    @Autowired
    MsgSetReplyService msgSetReplyService;


    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        List<CmdFFDataVO> cmdFFDataVOS = uplinkDataVO.getCmdFFDataVOS();
        if(CollectionUtils.isEmpty(cmdFFDataVOS))
            return;
        Map<String, MsgSetReplyDO> dbPublishMsgMap = findDbPublishMsggMap(cmdFFDataVOS);
        List<CmdFFDataVO> validMsgs = filterByDbMsg(cmdFFDataVOS, dbPublishMsgMap, uplinkDataVO);
        if(CollectionUtils.isEmpty(validMsgs))
            return;
        updateReplyInfo(cmdFFDataVOS, dbPublishMsgMap);

    }

    private void updateReplyInfo(List<CmdFFDataVO> cmdFFDataVOS, Map<String, MsgSetReplyDO> dbPublishMsgMap) {
        List<MsgSetReplyDO> replyMsgDates = cmdFFDataVOS.stream().map(cmdFFDataVO -> {
            MsgSetReplyDO dbPublishMsg = dbPublishMsgMap.get(cmdFFDataVO.msgKey());
            MsgSetReplyDO replyMsg = new MsgSetReplyDO();
            replyMsg.setId(dbPublishMsg.getId());
            replyMsg.setReplyTime(LocalDateTime.now());
            replyMsg.setReplyBody(JSONObject.toJSONString(cmdFFDataVO));
            return replyMsg;
        }).collect(Collectors.toList());
        log.info("收到设备回复消息,开始更新回复消息表 replyMsgDates={}",JSONObject.toJSONString(replyMsgDates));
        msgSetReplyService.updateBatchById(replyMsgDates);
    }

    private static List<CmdFFDataVO> filterByDbMsg(List<CmdFFDataVO> cmdFFDataVOS, Map<String, MsgSetReplyDO> dbPublishMsgMap, UplinkDataVO uplinkDataVO) {
        List<CmdFFDataVO> validMsgs = cmdFFDataVOS.stream().filter(cmdFFDataVO -> {
            MsgSetReplyDO dbPublishMsg = dbPublishMsgMap.get(cmdFFDataVO.msgKey());
            if (Objects.isNull(dbPublishMsg)) {
                log.error("超时消息{}", JSONObject.toJSONString(uplinkDataVO));
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return validMsgs;
    }

    private Map<String, MsgSetReplyDO> findDbPublishMsggMap(List<CmdFFDataVO> cmdFFDataVOS) {
        List<Short> midList = cmdFFDataVOS.stream().map(CmdFFDataVO::getMid).collect(Collectors.toList());
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


}
