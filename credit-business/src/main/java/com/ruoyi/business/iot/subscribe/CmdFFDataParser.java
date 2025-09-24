package com.ruoyi.business.iot.subscribe;

import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import com.ruoyi.business.util.DateUtil;

import java.nio.ByteBuffer;

/**
 * 解析reply 的消息
 */
public class CmdFFDataParser {


    public static UplinkCmdFFDataVO parse(ByteBuffer buffer){

        UplinkCmdFFDataVO cmdFFDataVO = UplinkCmdFFDataVO.builder()
                .mid(buffer.getShort())
                .readWriteFlag(buffer.get())
                .deviceTime(DateUtil.timestampToLocalDateTime(buffer.getInt() * 1000L))
                .build();
        if(ReadWriteEnum.READ.getCode().equals(cmdFFDataVO.getReadWriteFlag().intValue())){
            cmdFFDataVO.setDataHex(IotCommonUtil.bytesToHex(buffer.array()));
        }else {
            cmdFFDataVO.setResult(buffer.get());
        }
        return cmdFFDataVO;
    }

}
