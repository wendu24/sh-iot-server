package com.ruoyi.business.iot.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * 将单个字节（byte）转换为无符号整数（int）。
     *
     * @param b 要转换的字节。
     * @return 转换后的无符号整数值。
     */
    public static int byte2int(byte b) {
        // 使用按位与操作 & 0xFF 来消除符号扩展，确保结果为正数。
        // 如果 b 是负数，例如 -1，在不使用 & 0xFF 的情况下会转换为 -1。
        // 但通过 & 0xFF 操作，会得到 255。
        return b & 0xFF;
    }

    /**
     * 将单个字节（byte）转换为无符号整数（int）。
     *
     * @param b 要转换的字节。
     * @return 转换后的无符号整数值。
     */
    public static BigDecimal byte2Gigdecimal(byte b) {
        int intval = byte2int(b);
        return BigDecimal.valueOf(intval).divide(BigDecimal.TEN,1, RoundingMode.HALF_UP);
    }
    /**
     * 将单个字节（byte）转换为无符号整数（int）。
     *
     * @param b 要转换的字节。
     * @return 转换后的无符号整数值。
     */
    public static BigDecimal short2bigdecimal(short b) {
        return BigDecimal.valueOf(b).divide(BigDecimal.TEN,1, RoundingMode.HALF_UP);
    }

    /**
     * Converts a hexadecimal string to an integer.
     *
     * @param hexString The hexadecimal string to convert.
     * @return The integer value of the hexadecimal string.
     * @throws NumberFormatException if the string does not contain a parsable integer in hexadecimal format.
     */
    public static int hex2int(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

}
