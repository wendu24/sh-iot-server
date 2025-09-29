package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import org.springframework.stereotype.Component;

/**
 * 将回复消息或者上传的数据更新到设备表
 */
@Component
public class DeviceUpdateObserver extends AbstractUplinkMsgObserver {
    @Override
    public void handle(UplinkDataVO uplinkDataVO) {

    }


}
