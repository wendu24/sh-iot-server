package com.ruoyi.business.iot.publish;

import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CommonConstant;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;

/**
 * 完整数据打包 起始/结尾标识符/总长度/协议等
 */
@Slf4j
public class CompleteDataPackager {


    public static byte[] build(DtuDownDataVO dtuDownDataVO,String aesKey) throws Exception {
        byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
        byte[] dtuBytes = DtuDataPackager.buildCommand(dtuDownDataVO);
        byte[] encryptedBody = AesUtil.aesEncrypt(dtuBytes, aesKeyBytes);
        // 协议1字节 + 数字长度 + 校验和1字节 + 结束符1字节
        short length = (short) (1 + encryptedBody.length + 1 + 1);
        outputStream.write(CommonConstant.START_FLAG);
        outputStream.write(IotCommonUtil.shortToBytes(length));
        outputStream.write(CommonConstant.PROTOCOL);
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
