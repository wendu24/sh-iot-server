package com.ruoyi.business.iot.parser.udp;

import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.room.DeviceDataVO;
import com.ruoyi.business.iot.common.vo.room.HeaderDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class HeaderParser {


    public static HeaderDataVO parse(ByteBuffer buffer) {
        byte snLength = buffer.get();
        byte[] snByte = new byte[snLength];
        buffer.get(snByte);
        short dataLength = buffer.getShort();
        byte cmdCode = buffer.get();
        String deviceSn = IotCommonUtil.bytesToHex(snByte);
        HeaderDataVO headerDataVO = HeaderDataVO.builder()
                .deviceSn(deviceSn)
                .cmdCode(cmdCode)
                .build();
        if(CmdEnum.UPLINK_08.getCode().equals(cmdCode)){
            DeviceDataVO deviceDataVO = Cmd08DataParser.parse(deviceSn, buffer);
            deviceDataVO.setCmdCode((int)cmdCode);
            headerDataVO.setDeviceDataVO(deviceDataVO);
        }else {
            UplinkCmdFFDataVO uplinkCmdFFDataVO = CmdFFDataParser.parse(buffer, cmdCode);
            headerDataVO.setUplinkCmdFFDataVO(uplinkCmdFFDataVO);
        }
        return headerDataVO;
    }
}
