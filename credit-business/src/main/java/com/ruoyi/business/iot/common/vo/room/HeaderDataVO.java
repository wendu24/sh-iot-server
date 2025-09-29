package com.ruoyi.business.iot.common.vo.room;

import com.ruoyi.business.iot.common.vo.IotMsg;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderDataVO implements IotMsg {

    private Byte cmdCode;

    private String deviceSn;

    private DeviceDataVO deviceDataVO;

    private UplinkCmdFFDataVO uplinkCmdFFDataVO;


}
