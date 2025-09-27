package com.ruoyi.business.iot.subscribe;

import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Slf4j
public class RawDataParser {


    /**
     * 解析从设备接收到的原始数据包
     *
     * @param receivedData 从设备接收的原始字节数组
     * @return 解密并解析后的协议体数据（去除校验和）
     * @throws Exception 解析失败时抛出异常（如格式错误、校验失败等）
     */
    public static byte[] parse(byte[] receivedData, String aesKey) throws Exception {
        // Step 1: 验证消息起始和结束标志
        if (receivedData[0] != (byte) 0xAA ||
                receivedData[1] != (byte) 0x72 ||
                receivedData[receivedData.length - 1] != (byte) 0xDD) {
            throw new IllegalArgumentException("无效的起始或结束标志。期望起始为 AA 72，结束为 DD");
        }

        // Step 2: 校验外层 CS1 校验和
        byte receivedCs1 = receivedData[receivedData.length - 2]; // 倒数第二个字节是 CS1
        byte[] cs1DataRange = Arrays.copyOfRange(receivedData, 0, receivedData.length - 2);
        byte calculatedCs1 = IotCommonUtil.CS_Check(cs1DataRange,cs1DataRange.length);

        if (receivedCs1 != calculatedCs1) {
            throw new IllegalArgumentException("CS1 校验和验证失败。接收值: 0x" +
                    String.format("%02X", receivedCs1) + ", 计算值: 0x" + String.format("%02X", calculatedCs1));
        }

        // Step 3: 提取并解密协议体
        ByteBuffer buffer = ByteBuffer.wrap(receivedData).order(ByteOrder.LITTLE_ENDIAN);
//        short totalLength = buffer.getShort(2); // 从第2字节读取总长度（小端序）
        short totalLength = (short)(receivedData.length - 2 - 2);
        buffer.position(5); // 跳过：起始标志(2B) + 总长度(2B) + 协议版本(1B)

        // 加密体长度 = 总长度字段值 - 2（因为总长度包含 CS1 和 DD 两个字节）
        byte[] encryptedBody = new byte[totalLength - 3];
        buffer.get(encryptedBody); // 读取加密的协议体
        System.out.println("加密体 " + IotCommonUtil.bytesToHex(encryptedBody));
        // 使用 AES 解密协议体
        byte[] decryptedBody = AesUtil.aesDecrypt(encryptedBody, aesKey.getBytes("UTF-8"));

        // Step 4: 校验内层 CS2 校验和 [cite: 52]
        byte receivedCs2 = decryptedBody[decryptedBody.length - 1]; // 最后一个字节是 CS2
        byte[] cs2DataRange = Arrays.copyOfRange(decryptedBody, 0, decryptedBody.length - 1);
        byte calculatedCs2 = IotCommonUtil.CS_Check(cs2DataRange,cs2DataRange.length);

        if (receivedCs2 != calculatedCs2) {
            throw new IllegalArgumentException("CS2 校验和验证失败。接收值: 0x" +
                    String.format("%02X", receivedCs2) + ", 计算值: 0x" + String.format("%02X", calculatedCs2));
        }

        // Step 5: 返回解密后的协议体内容（去除末尾的 CS2）
        return Arrays.copyOfRange(decryptedBody, 0, decryptedBody.length - 1);
    }


}
