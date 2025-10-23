package com.ruoyi.business.iot.handler.uplink;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.util.RedisKeyUtil;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;



@Slf4j
@Component
public class RemoveCachedCmdObserver extends AbstractUplinkMsgObserver{



    @Autowired
    RedisCache redisCache;

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

        List<CmdFFDataVO> cmdFFDataVOS = uplinkDataVO.getCmdFFDataVOS();
        if(CollectionUtils.isEmpty(cmdFFDataVOS))
            return;

        String deviceSn = cmdFFDataVOS.get(0).getDeviceSn();
        byte cmdCode = cmdFFDataVOS.get(0).getCmdCode();
        String udpMsgCacheKey = RedisKeyUtil.udpMsgCacheKey(deviceSn);
        boolean result = redisCache.deleteCacheMapValue(udpMsgCacheKey, String.valueOf(cmdCode));
        log.info("收到回复后移除缓存命令deviceSn={} cmdCode={}  result={}",deviceSn,cmdCode , result);

    }
}
