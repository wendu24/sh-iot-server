package com.ruoyi.business.iot.protocol;

import com.ruoyi.business.iot.protocol.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.protocol.constant.CmdEnum;
import com.ruoyi.business.iot.protocol.vo.UplinkDataVO;
import com.ruoyi.business.iot.protocol.vo.UplinkCmd08DataVO;
import com.ruoyi.business.util.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class UplinkDataParser {


    /**
     * 解析从设备接收到的原始数据包
     *
     * @param receivedData 从设备接收的原始字节数组
     * @return 解密并解析后的协议体数据（去除校验和）
     * @throws Exception 解析失败时抛出异常（如格式错误、校验失败等）
     */
    public static byte[] parseReceivedData(byte[] receivedData,String aesKey) throws Exception {
        // Step 1: 验证消息起始和结束标志
        if (receivedData[0] != (byte) 0xAA ||
                receivedData[1] != (byte) 0x72 ||
                receivedData[receivedData.length - 1] != (byte) 0xDD) {
            throw new IllegalArgumentException("无效的起始或结束标志。期望起始为 AA 72，结束为 DD");
        }

        // Step 2: 校验外层 CS1 校验和 [cite: 48]
        byte receivedCs1 = receivedData[receivedData.length - 2]; // 倒数第二个字节是 CS1
        byte[] cs1DataRange = Arrays.copyOfRange(receivedData, 0, receivedData.length - 2);
        byte calculatedCs1 = IotCommonUtil.CS_Check(cs1DataRange,cs1DataRange.length);

        if (receivedCs1 != calculatedCs1) {
            throw new IllegalArgumentException("CS1 校验和验证失败。接收值: 0x" +
                    String.format("%02X", receivedCs1) + ", 计算值: 0x" + String.format("%02X", calculatedCs1));
        }

        // Step 3: 提取并解密协议体 [cite: 48, 49]
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

    /**
     * 注意：Java 的 ByteBuffer 默认使用大端序，但我们已通过 .order(ByteOrder.LITTLE_ENDIAN)
     *         // 显式设置为小端序，因此 getFloat()、getInt() 等方法会正确解析小端数据。
     * @param body
     * @return
     */
    public static UplinkDataVO parseData(byte[] body){
        ByteBuffer buffer = ByteBuffer.wrap(body).order(ByteOrder.LITTLE_ENDIAN);
        UplinkDataVO uplinkDataVO = parseCommonData(buffer);
        Integer cmdNum = uplinkDataVO.getCmdNum();
        for (int i = 0; i < cmdNum; i++) {
            byte[] snBytes = getDeviceSnBYte(buffer);
            ByteBuffer dataBuffer = getDataBuffer(buffer);
            byte cmd = dataBuffer.get();
            if(CmdEnum.UPLINK_08.getCode().equals(cmd)){
                UplinkCmd08DataVO uplinkCmd08DataVO = parseCmd08Data(dataBuffer);
                uplinkCmd08DataVO.setDeviceSn(IotCommonUtil.bytesToHex(snBytes));
                uplinkDataVO.addCmd08DataVOS(uplinkCmd08DataVO);
            }else {

            }

        }
        return uplinkDataVO;
    }


        /**
         * 根据标志字节从 ByteBuffer 中读取数据，并赋值给 DeviceStatusVO 对象。
         *
         * @param dataEnable1 标志字节。
         * @param buffer   包含所有数据的 ByteBuffer。
         */
    public static  void buildCmd08VOByDataEnable1(byte dataEnable1, ByteBuffer buffer,UplinkCmd08DataVO uplinkCmd08DataVO) {

            // 遍历标志字节的每一位，从低位开始
            for (int i = 0; i < 8; i++) {
                // 如果当前位为 1，则读取对应数据
                if (((dataEnable1 >> i) & 1) == 1) {
                    switch (i) {
                        case 0:

                            break;
                        case 1:
                            short valvePosition = buffer.getShort();
                            uplinkCmd08DataVO.setValvePosition(IotCommonUtil.short2bigdecimal(valvePosition));

                            break;
                        case 2:
                            short targetValvePosition = buffer.getShort();
                            uplinkCmd08DataVO.setTargetValvePosition(IotCommonUtil.short2bigdecimal(targetValvePosition));

                            break;
                        case 3:
                            short returnWaterTemperature = buffer.getShort();
                            uplinkCmd08DataVO.setReturnWaterPressure(IotCommonUtil.short2bigdecimal(returnWaterTemperature));

                            break;
                        case 4:
                            short targetReturnWaterTemperature = buffer.getShort();
                            uplinkCmd08DataVO.setTargetReturnWaterTemperature(IotCommonUtil.short2bigdecimal(targetReturnWaterTemperature));

                            break;
                        case 5:
                            short supplyWaterTemperature = buffer.getShort();
                            uplinkCmd08DataVO.setSupplyWaterTemperature(IotCommonUtil.short2bigdecimal(supplyWaterTemperature));

                            break;
                        case 6:
                            float flowRate = buffer.getFloat();
                            uplinkCmd08DataVO.setFlowRate(BigDecimal.valueOf(flowRate));
                            break;
                        case 7:
                            float heatOutput = buffer.getFloat();
                            uplinkCmd08DataVO.setHeatOutput(BigDecimal.valueOf(heatOutput));
                            break;
                    }
                }
            }
    }

        /**
         * 根据标志字节从 ByteBuffer 中读取数据，并赋值给 DeviceStatusVO 对象。
         *
         * @param dataEnable2 标志字节。
         * @param buffer   包含所有数据的 ByteBuffer。
         */
        public static void buildCmd08VOByDataEnable2(byte dataEnable2, ByteBuffer buffer,UplinkCmd08DataVO uplinkCmd08DataVO) {

            // 遍历标志字节的每一位，从低位开始
            for (int i = 0; i < 8; i++) {
                // 如果当前位为 1，则读取对应数据
                if (((dataEnable2 >> i) & 1) == 1) {
                    switch (i) {
                        case 0:
                            double totalFlowVolume = buffer.getDouble();
                            uplinkCmd08DataVO.setTotalFlowVolume(BigDecimal.valueOf(totalFlowVolume));
                            break;
                        case 1:
                            double totalHeatOutput = buffer.getDouble();
                            uplinkCmd08DataVO.setTotalHeatOutput(BigDecimal.valueOf(totalHeatOutput));
                            break;
                        case 2:
                            float supplyWaterPressure = buffer.getFloat();
                            uplinkCmd08DataVO.setSupplyWaterPressure(BigDecimal.valueOf(supplyWaterPressure));
                            break;
                        case 3:
                            float returnWaterPressure = buffer.getFloat();
                            uplinkCmd08DataVO.setReturnWaterPressure(BigDecimal.valueOf(returnWaterPressure));
                            break;
                        case 4:
                            short roomTemperature = buffer.getShort();
                            uplinkCmd08DataVO.setRoomTemperature(IotCommonUtil.short2bigdecimal(roomTemperature));
                            break;
                        case 5:
                            short targetRoomTemperature = buffer.getShort();
                            uplinkCmd08DataVO.setTargetRoomTemperature(IotCommonUtil.short2bigdecimal(targetRoomTemperature));
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                    }
                }
            }
        }

    public static UplinkCmd08DataVO parseCmd08Data(ByteBuffer buffer) {
        UplinkCmd08DataVO uplinkCmd08DataVO = UplinkCmd08DataVO.builder()
                .batteryLevel(IotCommonUtil.byte2int(buffer.get()))
                .abnormalTypes(buildAbnormalTypes(buffer.get()))
                .uplinkPeriod(buffer.getShort())
                .build();
        byte dataEnable1 = buffer.get();
        byte dataEnable2 = buffer.get();
        byte dataEnable3 = buffer.get();
        int timestamp = buffer.getInt();
        LocalDateTime collectionTime = DateUtil.timestampToLocalDateTime(timestamp * 1000L);
        uplinkCmd08DataVO.setCollectionTime(collectionTime);

        buildCmd08VOByDataEnable1(dataEnable1,buffer,uplinkCmd08DataVO);
        buildCmd08VOByDataEnable2(dataEnable2,buffer,uplinkCmd08DataVO);
        return uplinkCmd08DataVO;

    }


    private static List<AbnormalTypeEnum> buildAbnormalTypes(byte abnormal){
        ArrayList<AbnormalTypeEnum> abnormalTypeEnumArrayList = new ArrayList<AbnormalTypeEnum>();
        // 遍历标志字节的每一位，从低位开始
            for (int i = 0; i < 8; i++) {
                // 如果当前位为 1，则读取对应数据
                if (((abnormal >> i) & 1) == 1) {
                    switch (i) {
                        case 0:
                            abnormalTypeEnumArrayList.add(AbnormalTypeEnum.SENSOR_ABNORMALITY);
                            break;
                        case 1:
                            abnormalTypeEnumArrayList.add(AbnormalTypeEnum.VALVE_STALL);
                            break;
                        case 2:
                            abnormalTypeEnumArrayList.add(AbnormalTypeEnum.DISASSEMBLY);
                            break;
                    }
                }
            }
        return abnormalTypeEnumArrayList;
    }

    /**
     * 解析解密后的协议体数据，提取具体的字段信息。
     *
     * [cite_start]这是一个针对 CMD:08 指令数据的解析示例。[cite: 67]
     *
     * @param buffer 解密后的协议体字节数组（即原始报文去除加密层和校验字段后的内容）
     */
    public static UplinkDataVO parseCommonData(ByteBuffer buffer) {

        // 示例：解析 CMD:08 指令对应的数据格式 [cite: 67, 79]
        byte batteryLevel = buffer.get();        // 电池电量（单位：百分比 %）
        byte signalStrength = buffer.get();        // 信号强度
        byte[] iccIDBytes = new byte[11];
        buffer.get(iccIDBytes);
        byte replyFlag = buffer.get();
        byte cmdNum = buffer.get();

        return UplinkDataVO.builder()
                .batteryLevel(IotCommonUtil.byte2int(batteryLevel))
                .signalStrength(IotCommonUtil.byte2int(signalStrength))
                .iccID(IotCommonUtil.bytesToHex(iccIDBytes))
                .replyFlag(IotCommonUtil.byte2int(replyFlag))
                .cmdNum(IotCommonUtil.byte2int(cmdNum))
                .build()
                ;
        // 后续的解析逻辑通常包括：
        // 1. 读取“数据使能位”（Data Enable Bits），它是一个或多个字节，
        //    每一位表示后续某个特定数据字段是否存在或有效。
        // 2. 根据使能位的值，按协议规定的顺序和类型读取对应的数据点。
        // 3. 处理不同数据类型，如小端序的整数、浮点数（float）、双精度浮点数（double）等。
        //
        // 举例说明：
        // 如果协议规定在使能位之后有一个“瞬时流量”字段，类型为 float（4字节，小端序）：
        // float instantaneousFlow = buffer.getFloat(); // 自动按小端序解析
        // System.out.println("瞬时流量: " + instantaneousFlow + " m³/h");
        //
        //
    }


    private static ByteBuffer getDataBuffer(ByteBuffer buffer) {
        short dataLength = buffer.getShort();
        byte[] dataBytes = new byte[dataLength];
        buffer.get(dataBytes);
        ByteBuffer dataBuffer = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN);
        return dataBuffer;
    }

    private static byte[] getDeviceSnBYte(ByteBuffer buffer) {
        byte snLength = buffer.get();
        byte[] snBytes = new byte[snLength];
        buffer.get(snBytes);
        return snBytes;
    }

    public static void main(String[] args) {
        try{

            // --- 模拟平台接收设备上报数据并解析 ---
            System.out.println("\n--- 模拟平台接收上行数据并解析 ---");

            // 1. 模拟设备上报的数据包 (此数据包是按照协议格式手动构造的)
            // 示例数据：CMD:08 (采集数据上传)
            // 协议体内容：
            // 电池电量 (63) 告警 (02) 上报周期 (0A00)
            // 数据使能1 (02) 数据使能2 (04) 数据使能3 (00) 采集时间 (DE01125B)
            // 阀门开度 (5902)

            // 模拟一个包含上述数据的完整加密数据包
            // 注意：在实际中，此数据包会从 MQTT 接收。
            // 这里的十六进制字符串是按照协议格式预先构建的，用于演示解析过程。
//            String hexString = "aa722300200afa29ca8ab5a3f3422de940a0e8917c3d287965b1eb01be0a72d0141ba2ee3536dd";
//            String hexString = "aa7233002069afb3c941c9d7904a2304db25099c760d09458ddb7328c8bc840cbfcd6049179e0a722b4ee07241c977a5db72e03a7fafdd";
//            byte[] mockReceivedData = hexToBytes(hexString);
//
//            // 2. 调用 Parser 解析接收到的数据
//            byte[] decryptedBody = parseReceivedData(mockReceivedData);
//            System.out.println("解密后的数据 " + bytesToHex(decryptedBody));
//            // 3. 解析协议体中的具体业务数据
//            parseCmd08Data(decryptedBody);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
