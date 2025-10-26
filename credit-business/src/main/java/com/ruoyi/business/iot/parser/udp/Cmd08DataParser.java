package com.ruoyi.business.iot.parser.udp;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.uplink.RoomDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Cmd08DataParser {


    public static UdpCmd08DataVO parse(String deviceSn, ByteBuffer buffer){
        /**
         * 解析公共数据
         */
        UdpCmd08DataVO udpCmd08DataVO = parseCommonData(deviceSn, buffer);
        /**
         * 解析室温数据
         */
        List<RoomDataVO> roomDataVOList = parseRoomDates(buffer, udpCmd08DataVO.getDataNum());
        udpCmd08DataVO.setRoomDataVOList(roomDataVOList);
        log.info("解析出来的数据{}", JSONObject.toJSONString(udpCmd08DataVO));
        return udpCmd08DataVO;
    }

    public static UdpCmd08DataVO parseCommonData(String deviceSn, ByteBuffer buffer) {
        byte deviceVersion = buffer.get();
        byte warning = buffer.get();
        byte batteryLevel = buffer.get();
        short reportPeriod = buffer.getShort();
        byte signalStrength = buffer.get();

        byte[] ICCID = new byte[11];
        buffer.get(ICCID);

        byte dataNum = buffer.get();
        UdpCmd08DataVO udpCmd08DataVO = UdpCmd08DataVO.builder()
                .deviceSn(deviceSn)
                .deviceVersion(BigDecimal.valueOf(deviceVersion))
                .abnormalTypes(buildAbnormalTypes(warning))
                .batteryLevel(IotCommonUtil.byte2int(batteryLevel) == 255?null:IotCommonUtil.byte2int(batteryLevel))
                .reportPeriod(reportPeriod)
                .signalStrength(IotCommonUtil.byte2int(signalStrength))
                .iccId(IotCommonUtil.bytesToHex(ICCID))
                .dataNum(dataNum)
                .build();
        return udpCmd08DataVO;
    }

    private static  List<RoomDataVO> parseRoomDates(ByteBuffer buffer, byte dataNum) {
        List<RoomDataVO> roomDataVOList = new ArrayList<>(dataNum);
        for (int i = 0; i < dataNum; i++) {
            int collectTime = buffer.getInt();
            short roomTemperature = buffer.getShort();
            // 室内湿度
            short roomHumidity = buffer.getShort();
            RoomDataVO roomDataVO = RoomDataVO.builder()
                    .collectTime(DateUtil.timestampToLocalDateTime(collectTime * 1000L))
                    .roomHumidity(IotCommonUtil.short2bigdecimal(roomHumidity, BigDecimal.valueOf(100)))
                    .roomTemperature(IotCommonUtil.short2bigdecimal(roomTemperature,BigDecimal.valueOf(100)))
                    .build();
            roomDataVOList.add(roomDataVO);

        }
        return roomDataVOList;
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
                        abnormalTypeEnumArrayList.add(AbnormalTypeEnum.KEY_PRESS);

                        break;
                }
            }
        }
        return abnormalTypeEnumArrayList;
    }
}
