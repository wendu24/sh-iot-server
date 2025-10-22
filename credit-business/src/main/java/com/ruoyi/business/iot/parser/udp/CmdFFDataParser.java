package com.ruoyi.business.iot.parser.udp;

import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class CmdFFDataParser {


    public static CmdFFDataVO parse(ByteBuffer buffer, byte cmdCode){
        DownCmdEnum downCmdEnum = DownCmdEnum.getByCode(cmdCode);
        CmdFFDataVO cmdFFDataVO = CmdFFDataVO.builder()
                .mid(buffer.getShort())
                .cmdCode(cmdCode)
                .readWriteFlag(buffer.get())
//                .deviceTime(DateUtil.timestampToLocalDateTime(buffer.getInt() * 1000L))
                .build();
        if(ReadWriteEnum.READ.getCode().equals(cmdFFDataVO.getReadWriteFlag().intValue())){
            parseDataByCmdType(buffer, downCmdEnum, cmdFFDataVO);
        }else {
            cmdFFDataVO.setResult(buffer.get());
        }
        return cmdFFDataVO;
    }

    /**
     * 根据命令类型解析数据
     * @param buffer
     * @param downCmdEnum
     * @param cmdFFDataVO
     */
    private static void parseDataByCmdType(ByteBuffer buffer, DownCmdEnum downCmdEnum, CmdFFDataVO cmdFFDataVO) {
        if(downCmdEnum.getDataClazz() == Float.class){
            cmdFFDataVO.setData(String.valueOf(buffer.getShort()));
        } else if (downCmdEnum.getDataClazz() == String.class) {
            // 读取所有剩余字节
            int remainingBytes = buffer.remaining();
            byte[] remainingData = new byte[remainingBytes];
            buffer.get(remainingData);
            cmdFFDataVO.setData(IotCommonUtil.paddedBytesToString(remainingData));
        }else if(downCmdEnum.getDataClazz() == Byte.class){
            cmdFFDataVO.setData(String.valueOf(buffer.get()));
        }else {
            log.info("解析设备回复消息时, 不支持的数据类型={}",downCmdEnum.getCode());
        }
    }

}
