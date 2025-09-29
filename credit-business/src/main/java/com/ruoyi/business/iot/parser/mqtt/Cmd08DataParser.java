package com.ruoyi.business.iot.parser.mqtt;

import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmd08DataVO;
import com.ruoyi.business.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cmd08DataParser {


    /**
     * 解析cmd08数据
     * @param buffer
     * @return
     */
    public static UplinkCmd08DataVO parse(ByteBuffer buffer) {
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

    /**
     * 根据数据使能1从 ByteBuffer 中读取数据，并赋值给 DeviceStatusVO 对象。
     *
     * @param dataEnable1 标志字节。
     * @param buffer   包含所有数据的 ByteBuffer。
     */
    private static void buildCmd08VOByDataEnable1(byte dataEnable1, ByteBuffer buffer, UplinkCmd08DataVO uplinkCmd08DataVO) {

        // 遍历标志字节的每一位，从低位开始
        for (int i = 0; i < 8; i++) {
            // 如果当前位为 1，则读取对应数据
            if (((dataEnable1 >> i) & 1) == 1) {
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        short valvePosition = buffer.getShort();
                        uplinkCmd08DataVO.setValvePosition(IotCommonUtil.short2bigdecimal(valvePosition).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 2:
                        short targetValvePosition = buffer.getShort();
                        uplinkCmd08DataVO.setTargetValvePosition(IotCommonUtil.short2bigdecimal(targetValvePosition).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 3:
                        short returnWaterTemperature = buffer.getShort();
                        uplinkCmd08DataVO.setReturnWaterPressure(IotCommonUtil.short2bigdecimal(returnWaterTemperature).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 4:
                        short targetReturnWaterTemperature = buffer.getShort();
                        uplinkCmd08DataVO.setTargetReturnWaterTemperature(IotCommonUtil.short2bigdecimal(targetReturnWaterTemperature).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 5:
                        short supplyWaterTemperature = buffer.getShort();
                        uplinkCmd08DataVO.setSupplyWaterTemperature(IotCommonUtil.short2bigdecimal(supplyWaterTemperature).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 6:
                        float flowRate = buffer.getFloat();
                        uplinkCmd08DataVO.setFlowRate(BigDecimal.valueOf(flowRate).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 7:
                        float heatOutput = buffer.getFloat();
                        uplinkCmd08DataVO.setHeatOutput(BigDecimal.valueOf(heatOutput).setScale(1, RoundingMode.HALF_UP));
                        break;
                }
            }
        }
    }

    /**
     * 根据数据使能2从 ByteBuffer 中读取数据，并赋值给 DeviceStatusVO 对象。
     *
     * @param dataEnable2 标志字节。
     * @param buffer   包含所有数据的 ByteBuffer。
     */
    private static void buildCmd08VOByDataEnable2(byte dataEnable2, ByteBuffer buffer,UplinkCmd08DataVO uplinkCmd08DataVO) {

        // 遍历标志字节的每一位，从低位开始
        for (int i = 0; i < 8; i++) {
            // 如果当前位为 1，则读取对应数据
            if (((dataEnable2 >> i) & 1) == 1) {
                switch (i) {
                    case 0:
                        double totalFlowVolume = buffer.getDouble();
                        uplinkCmd08DataVO.setTotalFlowVolume(BigDecimal.valueOf(totalFlowVolume).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 1:
                        double totalHeatOutput = buffer.getDouble();
                        uplinkCmd08DataVO.setTotalHeatOutput(BigDecimal.valueOf(totalHeatOutput).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 2:
                        float supplyWaterPressure = buffer.getFloat();
                        uplinkCmd08DataVO.setSupplyWaterPressure(BigDecimal.valueOf(supplyWaterPressure).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 3:
                        float returnWaterPressure = buffer.getFloat();
                        uplinkCmd08DataVO.setReturnWaterPressure(BigDecimal.valueOf(returnWaterPressure).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 4:
                        short roomTemperature = buffer.getShort();
                        uplinkCmd08DataVO.setRoomTemperature(IotCommonUtil.short2bigdecimal(roomTemperature).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 5:
                        short targetRoomTemperature = buffer.getShort();
                        uplinkCmd08DataVO.setTargetRoomTemperature(IotCommonUtil.short2bigdecimal(targetRoomTemperature).setScale(1, RoundingMode.HALF_UP));
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                }
            }
        }
    }


    /**
     * 通过异常byte解析设备包含哪些异常
     * @param abnormal
     * @return
     */
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

}
