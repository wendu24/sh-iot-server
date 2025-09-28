package com.ruoyi.business.iot.parser.udp;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.constant.AbnormalTypeEnum;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.room.DeviceDataVO;
import com.ruoyi.business.iot.common.vo.room.RoomDataVO;
import com.ruoyi.business.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Cmd08DataParser {


    public static DeviceDataVO parse(ByteBuffer buffer){

        byte snLength = buffer.get();
        byte[] snByte = new byte[snLength];
        buffer.get(snByte);
        short dataLength = buffer.getShort();
        byte cmdCode = buffer.get();

        byte deviceVersion = buffer.get();
        byte warning = buffer.get();
        byte batteryLevel = buffer.get();
        short reportPeriod = buffer.getShort();
        byte signalStrength = buffer.get();

        byte[] ICCID = new byte[11];
        buffer.get(ICCID);

        byte dataNum = buffer.get();
        DeviceDataVO deviceDataVO = DeviceDataVO.builder()
                .deviceSn(IotCommonUtil.bytesToHex(snByte))
                .deviceVersion(IotCommonUtil.byte2Gigdecimal(deviceVersion))
                .abnormalTypes(buildAbnormalTypes(warning))
                .batteryLevel(IotCommonUtil.byte2int(batteryLevel))
                .reportPeriod(reportPeriod)
                .signalStrength(IotCommonUtil.byte2int(signalStrength))
                .iccId(IotCommonUtil.bytesToHex(ICCID))
                .build();

        List<RoomDataVO> roomDataVOList = new ArrayList<>(dataNum);
        for (int i = 0; i < dataNum; i++) {
            int collectTime = buffer.getInt();
            short roomTemperature = buffer.getShort();
            // 室内湿度
            short roomHumidity = buffer.getShort();
            RoomDataVO roomDataVO = RoomDataVO.builder()
                    .collectTime(DateUtil.timestampToLocalDateTime(collectTime * 1000L))
                    .roomHumidity(IotCommonUtil.short2bigdecimal(roomHumidity))
                    .roomTemperature(IotCommonUtil.short2bigdecimal(roomTemperature))
                    .build();
            roomDataVOList.add(roomDataVO);

        }
        deviceDataVO.setRoomDataVOList(roomDataVOList);
        log.info("解析出来的数据{}", JSONObject.toJSONString(deviceDataVO));
        return deviceDataVO;
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
