package com.ruoyi.business.iot.packager;

import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Slf4j
@Deprecated
public class DownlinkDataPackager {




    /**
     * 构建下发命令的消息体
     * @param cmdEnum CMD
     * @param mid 消息编号
     * @param readWriteEnum 读写标识
     * @param data 数据
     */
    @Deprecated
    public static byte[] buildShortCommand(
                                    CmdEnum cmdEnum,
                                    Short mid,
                                    ReadWriteEnum readWriteEnum,
                                    Short data
    ) throws IOException {
            // 1. 定义下发命令的参数

//            byte commandCount = 1;
//            byte[] sn = IotProtocolCommonUtil.hexToBytes("102110042509081201"); // 有时候是设备的SN，有时候是DTU的SN
//            byte[] sn = IotProtocolCommonUtil.hexToBytes(deviceSn); // 有时候是设备的SN，有时候是DTU的SN
//            byte[] sn = hexToBytes("105110042509083201"); // DYU
//            byte[] sn = hexToBytes("105110042400003201"); // 核对

            // 2. 构建命令体：CMD:23(设置上报间隔)
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 写入 CMD (1 字节)
            outputStream.write(cmdEnum.getCode());
            // 写入 MID (2 字节)
            outputStream.write(IotCommonUtil.shortToBytes(mid));
            // 写入读写标志 (1 字节) 这个方法只会写入最低八位
            outputStream.write(readWriteEnum.getCode());
            if(ReadWriteEnum.WRITE.getCode().equals(readWriteEnum.getCode())){
                // 写入数据 (2 字节)
                outputStream.write(IotCommonUtil.shortToBytes(data));
            }
            byte[] cmdData = outputStream.toByteArray();
            return cmdData;
            // 3. 调用 Handler 构建完整的下发消息
//            byte[] downlinkMessage = buildDownlinkMessage(timestamp, commandCount, sn, cmdData,aesKey);

    }

    /**
     * 构建整个消息体，包括加密
     * @param timestamp
     * @param commandCount 具体下发的命令及数据
     * @param sn
     * @param cmdData
     * @return
     * @throws Exception
     */
    @Deprecated
    public static byte[] buildDownlinkMessage(int timestamp, byte commandCount, byte[] sn, byte[] cmdData,String aesKey) throws Exception {
        byte[] aesKeyBytes = aesKey.getBytes("UTF-8");
        // Step 1: 构建未加密的协议主体部分 [cite: 92]
        // 分配足够空间：时间戳(4) + 命令数(1) + SN长度(1) + SN + 数据长度(2) + 命令数据 + CS2(1)
        ByteBuffer bodyBuffer = ByteBuffer.allocate(4 + 1 + 1 + sn.length + 2 + cmdData.length + 1)
                .order(ByteOrder.LITTLE_ENDIAN);

        bodyBuffer.put(IotCommonUtil.intToBytes(timestamp));      // 时间戳 [cite: 92, 96]
        bodyBuffer.put(commandCount);               // 命令计数 [cite: 92, 96]
        bodyBuffer.put((byte) sn.length);           // SN 长度 [cite: 92]
        bodyBuffer.put(sn);                         // SN 内容 [cite: 92]
        bodyBuffer.put(IotCommonUtil.shortToBytes((short) cmdData.length)); // 命令数据长度 [cite: 92]
        bodyBuffer.put(cmdData);                    // 命令数据本身 [cite: 92]

        // 提取协议体内容
        byte[] bodyContent = new byte[bodyBuffer.position()];
        bodyBuffer.rewind();
        bodyBuffer.get(bodyContent);

        // Step 2: 计算协议体的 CS2 校验和 [cite: 92]
//        byte cs2 = calculateChecksum(bodyContent);
        System.out.println("明文" + IotCommonUtil.bytesToHex(bodyContent));
        byte cs2 = IotCommonUtil.CS_Check(bodyContent,bodyContent.length);
        System.out.println("cs2=" + cs2);
        // Step 3: 将 CS2 附加到协议体末尾，并整体加密 [cite: 92]
        byte[] finalBody = Arrays.copyOf(bodyContent, bodyContent.length + 1);
        finalBody[finalBody.length - 1] = cs2; // 在最后添加 CS2
        System.out.println(IotCommonUtil.bytesToHex(Arrays.copyOf(finalBody,finalBody.length)));
        byte[] encryptedBody = AesUtil.aesEncrypt(finalBody, aesKeyBytes); // 使用预设密钥加密

        // Step 4: 构建完整的外层消息包 [cite: 88]
        // 总长度 = 加密体长度 + CS1(1) + 结束符(1)
        ByteBuffer messageBuffer = ByteBuffer.allocate(2 + 2 + 1 + encryptedBody.length + 1 + 1)
                .order(ByteOrder.LITTLE_ENDIAN);

        messageBuffer.put(new byte[]{(byte) 0xAA, (byte) 0x72}); // 起始标志 AA 72 [cite: 88]
        messageBuffer.put(IotCommonUtil.shortToBytes((short) (encryptedBody.length + 1 + 1 + 1))); // 总长度字段（含 协议版本 CS1 和 DD）[cite: 88]
        messageBuffer.put((byte) 0x20);                         // 协议版本号 0x20 [cite: 88]
        messageBuffer.put(encryptedBody);                       // 加密后的协议体 [cite: 88]

        // Step 5: 计算外层消息的 CS1 校验和（从第2字节开始到CS1前）[cite: 88]
        byte[] bytes = Arrays.copyOfRange(messageBuffer.array(), 0, messageBuffer.position());
        System.out.println(IotCommonUtil.bytesToHex(bytes));
        byte cs1 = IotCommonUtil.CS_Check(bytes,bytes.length);
        messageBuffer.put(cs1); // 添加 CS1 校验和

        messageBuffer.put((byte) 0xDD); // 结束标志 DD [cite: 88]

        return messageBuffer.array(); // 返回完整消息字节数组
    }
}
