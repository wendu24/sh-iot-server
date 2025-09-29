package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.iot.common.vo.UplinkDataVO;

public interface UplinkMsgObserver {

    public void handle( UplinkDataVO uplinkDataVO);


}
