package com.ruoyi.business.iot.publish;

import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 命令数据打包
 */
@Slf4j
public class CmdDataPackager {


    /**
     * 构建下发命令的消息体
     * @param commonDownDataVO 数据
     */
    public static byte[] buildCommand(CommonDownDataVO commonDownDataVO) throws IOException {
        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
        // 数据体
        byte[] commandData = buildShortCommandBody(commonDownDataVO);
        byte[] sn = IotCommonUtil.hexToBytes(commonDownDataVO.getDeviceSn());
        outputStream.write((byte)sn.length);
        outputStream.write(sn);
        // 命令数据长度
        outputStream.write(IotCommonUtil.shortToBytes((short) commandData.length));
        outputStream.write(commandData);
        return outputStream.toByteArray();
    }



    /**
     * 构建下发命令的消息体
     * @param commonDownDataVO 数据
     */
    private static byte[] buildShortCommandBody(CommonDownDataVO commonDownDataVO) throws IOException {
        byte cmdCode = commonDownDataVO.getCmdCode();
        // 2. 构建命令体：CMD:23(设置上报间隔)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 写入 CMD (1 字节)
        outputStream.write(cmdCode);
        // 写入 MID (2 字节)
        outputStream.write(IotCommonUtil.shortToBytes(commonDownDataVO.getMid()));
        // 写入读写标志 (1 字节) 这个方法只会写入最低八位
        outputStream.write(commonDownDataVO.getReadWriteFlag());
        if(ReadWriteEnum.WRITE.getCode().equals(commonDownDataVO.getReadWriteFlag())){
            writeData(outputStream,commonDownDataVO);
        }
        byte[] cmdData = outputStream.toByteArray();
        return cmdData;
    }

    private static void writeData(ByteArrayOutputStream outputStream,CommonDownDataVO commonDownDataVO) throws IOException {
        CmdEnum cmdEnum = CmdEnum.getByCode(commonDownDataVO.getCmdCode());
        if(cmdEnum.getDataClazz() == Float.class ){
            // 写入数据 (2 字节)
            outputStream.write(IotCommonUtil.shortToBytes(commonDownDataVO.getData().shortValue()));
        }else if(cmdEnum.getDataClazz() == String.class ){
            byte[] bytes = commonDownDataVO.getDataStr().getBytes("utf-8");
            outputStream.write(bytes);
            // 如果字符串长度不足 50 字节，用 0 填充
            int paddingLength = cmdEnum.getDataLength() - bytes.length;
            if (paddingLength > 0) {
                outputStream.write(new byte[paddingLength]);
            }
        }else {
            log.error("暂不支持的数据类型");
        }

    }
}
