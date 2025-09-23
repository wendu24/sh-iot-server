package com.ruoyi.business.iot.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IotCommonUtil {


    // Helper method to convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        ByteBuffer buffer = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i += 2) {
            String sub = hex.substring(i, i + 2);
            buffer.put((byte) Integer.parseInt(sub, 16));
        }
        return buffer.array();
    }


    /**
     * 计算字节数组的累加型校验和（CS Checksum）。
     * 将所有字节相加，结果对 256 取模（保留低 8 位）。
     *
     * @param buf 字节数组
     * @param len 长度（通常为 buf.length）
     * @return 校验和
     */
    public static byte CS_Check(byte[] buf, int len) {
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += buf[i] & 0xFF; // 将 byte 转为无符号整数
        }
        return (byte) (sum & 0xFF); // 确保返回值在 -128 到 127 范围内，符合 byte 类型
    }




    /**
     * 将 short 类型整数转换为小端序（Little-Endian）的 2 字节数组
     * @param value 输入的 short 值
     * @return 长度为 2 的字节数组
     */
    public static byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }

    /**
     * 将 int 类型整数转换为小端序（Little-Endian）的 4 字节数组
     * @param value 输入的 int 值
     * @return 长度为 4 的字节数组
     */
    public static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }
}
