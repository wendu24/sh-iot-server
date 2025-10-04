package com.ruoyi.business.iot.packager.mqtt;

import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

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
        byte[] commandData = buildCommandBody(commonDownDataVO);
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
    private static byte[] buildCommandBody(CommonDownDataVO commonDownDataVO) throws IOException {
        byte cmdCode = commonDownDataVO.getCmdCode();
        // 2. 构建命令体：CMD:23(设置上报间隔)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 写入 CMD (1 字节)
        outputStream.write(cmdCode);
        // 写入 MID (2 字节)
        outputStream.write(IotCommonUtil.shortToBytes(commonDownDataVO.getMid()));
        // 写入读写标志 (1 字节) 这个方法只会写入最低八位
        outputStream.write(commonDownDataVO.getReadWriteFlag());
        if(ReadWriteEnum.WRITE.getCode().equals(commonDownDataVO.getReadWriteFlag())
         || ReadWriteEnum.RESPONSE.getCode().equals(commonDownDataVO.getReadWriteFlag())
        ){
            writeData(outputStream,commonDownDataVO);
        }
        byte[] cmdData = outputStream.toByteArray();
        return cmdData;
    }

    private static void writeData(ByteArrayOutputStream outputStream,CommonDownDataVO commonDownDataVO) throws IOException {
        DownCmdEnum downCmdEnum = DownCmdEnum.getByCode(commonDownDataVO.getCmdCode());
        if(downCmdEnum.getDataClazz() == Float.class ){
            // 写入数据 (2 字节)
            outputStream.write(IotCommonUtil.shortToBytes(commonDownDataVO.getData().shortValue()));

        }else if(downCmdEnum.getDataClazz() == String.class ){
            byte[] bytes = commonDownDataVO.getDataStr().getBytes("utf-8");
            outputStream.write(bytes);
            // 如果字符串长度不足 50 字节，用 0 填充
            int paddingLength = downCmdEnum.getDataLength() - bytes.length;
            if (paddingLength > 0) {
                outputStream.write(new byte[paddingLength]);
            }
        }else if(downCmdEnum.getDataClazz() == Date.class){
            int time = (int)(System.currentTimeMillis() / 1000);
            byte[] timeBytes = IotCommonUtil.intToBytes(time);
            outputStream.write(timeBytes);
            outputStream.write((byte)0);
            log.info("time={}",time);
            log.info("timeBytes={}",IotCommonUtil.bytesToHex(timeBytes));

        }else {
            log.error("暂不支持的数据类型");
        }

    }

    public static void main(String[] args) {
        int time = (int)(System.currentTimeMillis() / 1000);
        System.out.println(time);
        byte[] timeBytes = IotCommonUtil.intToBytes(time);
        System.out.println(IotCommonUtil.bytesToHex(timeBytes));
    }
}
