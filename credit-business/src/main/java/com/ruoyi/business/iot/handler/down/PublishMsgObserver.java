package com.ruoyi.business.iot.handler.down;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.domain.MsgSetReplyDO;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.service.MsgSetReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@Slf4j
public class PublishMsgObserver extends AbstractDownMsgObserver {

    @Autowired
    MsgSetReplyService msgSetReplyService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public void handle( DtuDownDataVO dtuDownDataVO) {
        ArrayList<MsgSetReplyDO> msgSetReplyList = new ArrayList<>();
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> {
            if(commonDownDataVO.getCmdCode() == DownCmdEnum.DOWNLINK_FF.getCode() || commonDownDataVO.getCmdCode() == DownCmdEnum.DOWNLINK_UDP_RESPONSE.getCode())
                return;
            DeviceDO deviceDO ;
            try {
                deviceDO = deviceService.findByDeviceSn(commonDownDataVO.getDeviceSn());
            } catch (Exception e) {
                log.error("保存下发的数据时出错",e);
                return;
            }
            MsgSetReplyDO msgSetReplyDO = new MsgSetReplyDO();
            msgSetReplyDO.setDeviceSn(commonDownDataVO.getDeviceSn());
            msgSetReplyDO.setCmdCode(((Byte)commonDownDataVO.getCmdCode()).intValue());
            msgSetReplyDO.setMid(commonDownDataVO.getMid().intValue());
//            msgSetReplyDO.setTopic(topic);
            msgSetReplyDO.setPublishTime(dtuDownDataVO.getPublishTime());
            msgSetReplyDO.setMsgBody(JSONObject.toJSONString(commonDownDataVO));
            msgSetReplyDO.setCreateTime(LocalDateTime.now());
            msgSetReplyDO.setCommunityId(deviceDO.getCommunityId());
            msgSetReplyDO.setCommunityName(deviceDO.getCommunityName());
            msgSetReplyList.add(msgSetReplyDO);
        });
        if(CollectionUtils.isEmpty(msgSetReplyList))
            return;
        msgSetReplyService.saveBatch(msgSetReplyList);
    }

}
