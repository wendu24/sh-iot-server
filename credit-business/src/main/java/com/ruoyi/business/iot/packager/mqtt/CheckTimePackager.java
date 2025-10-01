//package com.ruoyi.business.iot.packager.mqtt;
//
//import com.ruoyi.business.iot.common.util.IotCommonUtil;
//import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//public class CheckTimePackager {
//    public static byte[] buildCommand(CommonDownDataVO commonDownDataVO) throws IOException {
//        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
//        byte[] sn = IotCommonUtil.hexToBytes(commonDownDataVO.getDeviceSn());
//
//        // 数据体
//        outputStream.write((byte)sn.length);
//        outputStream.write(sn);
//        // 命令数据长度
//        outputStream.write(IotCommonUtil.shortToBytes((short) commandData.length));
//        outputStream.write(commandData);
//        return outputStream.toByteArray();
//
//
//        // 数据体
//        outputStream.write(IotCommonUtil.shortToBytes(mid));
//        outputStream.write((byte)2);
//        int time = (int)System.currentTimeMillis() / 1000;
//
//        outputStream.write(IotCommonUtil.intToBytes(time));
//        // 命令数据长度
//        outputStream.write((byte)0);
//        return outputStream.toByteArray();
//    }
//}
