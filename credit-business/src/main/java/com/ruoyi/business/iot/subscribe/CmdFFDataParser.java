package com.ruoyi.business.iot.subscribe;

import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import com.ruoyi.business.util.DateUtil;

import java.nio.ByteBuffer;

/**
 * 解析reply 的消息
 */
public class CmdFFDataParser {


    public static UplinkCmdFFDataVO parse(ByteBuffer buffer,byte cmdCode){
        CmdEnum cmdEnum = CmdEnum.getByCode(cmdCode);
        UplinkCmdFFDataVO cmdFFDataVO = UplinkCmdFFDataVO.builder()
                .mid(buffer.getShort())
                .cmdCode(cmdCode)
                .readWriteFlag(buffer.get())
                .deviceTime(DateUtil.timestampToLocalDateTime(buffer.getInt() * 1000L))
                .build();
        if(ReadWriteEnum.READ.getCode().equals(cmdFFDataVO.getReadWriteFlag().intValue())){
            parseDataByCmdType(buffer, cmdEnum, cmdFFDataVO);
        }else {
            cmdFFDataVO.setResult(buffer.get());
        }
        return cmdFFDataVO;
    }

    /**
     * 根据命令类型解析数据
     * @param buffer
     * @param cmdEnum
     * @param cmdFFDataVO
     */
    private static void parseDataByCmdType(ByteBuffer buffer, CmdEnum cmdEnum, UplinkCmdFFDataVO cmdFFDataVO) {
        if(cmdEnum.getDataClazz() == Float.class){
            byte[] bytes = IotCommonUtil.shortToBytes(buffer.getShort());
            cmdFFDataVO.setDataBytes(bytes);
        } else if (cmdEnum.getDataClazz() == String.class) {
            // 读取所有剩余字节
            int remainingBytes = buffer.remaining();
            byte[] remainingData = new byte[remainingBytes];
            buffer.get(remainingData);
            cmdFFDataVO.setDataBytes(remainingData);
        }
    }

}
