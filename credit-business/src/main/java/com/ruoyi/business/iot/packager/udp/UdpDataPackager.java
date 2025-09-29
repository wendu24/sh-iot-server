package com.ruoyi.business.iot.packager.udp;

import com.ruoyi.business.iot.common.constant.CommonConstant;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;

@Slf4j
public class UdpDataPackager {

    public static byte[] build(DtuDownDataVO dtuDownDataVO,String sn, String aesKey) throws Exception {
        byte[] snBytes = IotCommonUtil.hexToBytes(sn);
        byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
        byte[] dtuBytes = CheckSumPackager.buildCommand(dtuDownDataVO);

        byte[] encryptedBody = AesUtil.aesEncrypt(dtuBytes, aesKeyBytes);
        // 协议1字节 + sn长度+ sn + 数字长度 + 校验和1字节 + 结束符1字节
        short length = (short) (1 + 1 + snBytes.length +  encryptedBody.length + 1 + 1);
        outputStream.write(CommonConstant.START_FLAG);
        outputStream.write(IotCommonUtil.shortToBytes(length));
        outputStream.write(CommonConstant.PROTOCOL_10);
        outputStream.write((byte)snBytes.length);
        outputStream.write(snBytes);
        outputStream.write(encryptedBody);
        byte[] byteArray = outputStream.toByteArray();
        byte cs1 = IotCommonUtil.CS_Check(byteArray, byteArray.length);
        outputStream.write(cs1);
        outputStream.write(CommonConstant.END_FLAG);
        byte[] bytes = outputStream.toByteArray();
        log.info("下发消息 {}",IotCommonUtil.bytesToHex(bytes));
        return bytes;

    }

}
