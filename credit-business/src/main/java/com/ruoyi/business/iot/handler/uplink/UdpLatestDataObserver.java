package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UdpLatestDataObserver extends AbstractUplinkMsgObserver{


    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

    }
}
