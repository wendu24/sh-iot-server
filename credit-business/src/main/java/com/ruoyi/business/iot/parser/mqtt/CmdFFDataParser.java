package com.ruoyi.business.iot.parser.mqtt;

import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.util.DateUtil;

import java.nio.ByteBuffer;

/**
 * 解析reply 的消息
 */
public class CmdFFDataParser {


    public static CmdFFDataVO parse(ByteBuffer buffer, byte cmdCode){
        DownCmdEnum downCmdEnum = DownCmdEnum.getByCode(cmdCode);
        CmdFFDataVO cmdFFDataVO = CmdFFDataVO.builder()
                .mid(buffer.getShort())
                .cmdCode(cmdCode)
                .readWriteFlag(buffer.get())
                .deviceTime(DateUtil.timestampToLocalDateTime(buffer.getInt() * 1000L))
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
        }
    }

    public static void main(String[] args) {
        byte[] aa = {51,57,46,57,57,46,49,56,48,46,49,52,58,49,56,56,51,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        System.out.println(IotCommonUtil.paddedBytesToString(aa));
    }

}
