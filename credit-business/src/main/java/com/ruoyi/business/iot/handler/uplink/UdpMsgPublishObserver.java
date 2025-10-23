package com.ruoyi.business.iot.handler.uplink;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ruoyi.business.constant.DeviceTypeEnum;
import com.ruoyi.business.domain.DeviceDO;
import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.service.DeviceService;
import com.ruoyi.business.util.RedisKeyUtil;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 收到上传数据时 批量下发, reply时不触发 .
 */
@Slf4j
@Component
public class UdpMsgPublishObserver extends AbstractUplinkMsgObserver{

    @Autowired
    RedisCache redisCache;

    @Autowired
    UdpService udpService;

    @Autowired
    DeviceService deviceService;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        if(Objects.isNull(udpCmd08DataVO))
            return;


        String deviceSn = udpCmd08DataVO.getDeviceSn();

        String udpMsgCacheKey = RedisKeyUtil.udpMsgCacheKey(deviceSn);

        /**
         * 发布消息
         */
        publishMsg(udpMsgCacheKey, deviceSn);
    }

    private void publishMsg(String udpMsgCacheKey, String deviceSn) {
        Map<String, DtuDownDataVO> cachedMsg = redisCache.getCacheMap(udpMsgCacheKey);
        if(CollectionUtils.isEmpty(cachedMsg))
            return;

        cachedMsg.forEach((cmdCode,oneMsg)->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException("发布消息时被中断" + deviceSn);
            }
            udpService.sendCommandAsync(deviceSn, oneMsg);
        });
    }


}
