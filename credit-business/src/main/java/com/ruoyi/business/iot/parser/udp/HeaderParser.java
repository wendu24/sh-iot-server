package com.ruoyi.business.iot.parser.udp;

import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.room.DeviceDataVO;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class HeaderParser {


    public static DeviceDataVO parse(ByteBuffer buffer) {
        byte snLength = buffer.get();
        byte[] snByte = new byte[snLength];
        buffer.get(snByte);
        short dataLength = buffer.getShort();
        byte cmdCode = buffer.get();
        String deviceSn = IotCommonUtil.bytesToHex(snByte);
        if(CmdEnum.UPLINK_08.getCode().equals(cmdCode)){
            return Cmd08DataParser.parse(deviceSn,buffer);

        }else {
            return null;
        }

    }
}
