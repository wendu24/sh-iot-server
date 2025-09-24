package com.ruoyi.business.iot.subscribe;

import com.ruoyi.business.iot.common.vo.UplinkCmdFFDataVO;
import com.ruoyi.business.util.DateUtil;

import java.nio.ByteBuffer;

public class CmdFFDataParser {


    public static UplinkCmdFFDataVO parse(ByteBuffer buffer){

        return UplinkCmdFFDataVO.builder()
                .mid(buffer.getShort())
                .readWriteFlag(buffer.get())
                .deviceTime(DateUtil.timestampToLocalDateTime(buffer.getInt()*1000L))
                .result(buffer.get())
                .build()
                ;
    }

}
