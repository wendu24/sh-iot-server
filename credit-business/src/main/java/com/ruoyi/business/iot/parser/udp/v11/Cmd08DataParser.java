package com.ruoyi.business.iot.parser.udp.v11;

import com.alibaba.fastjson2.JSONObject;
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

/**
 * V1.1版本 增加了功率字段
 */
@Slf4j
public class Cmd08DataParser extends com.ruoyi.business.iot.parser.udp.Cmd08DataParser {


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
        log.info("1.1版本解析出来的数据{}", JSONObject.toJSONString(udpCmd08DataVO));
        return udpCmd08DataVO;
    }


    private static List<RoomDataVO> parseRoomDates(ByteBuffer buffer, byte dataNum) {
        List<RoomDataVO> roomDataVOList = new ArrayList<>(dataNum);
        for (int i = 0; i < dataNum; i++) {
            int collectTime = buffer.getInt();
            short roomTemperature = buffer.getShort();
            // 室内湿度
            short roomHumidity = buffer.getShort();
            // 功率
            short watt = buffer.getShort();
            RoomDataVO roomDataVO = RoomDataVO.builder()
                    .collectTime(DateUtil.timestampToLocalDateTime(collectTime * 1000L))
                    .roomHumidity(IotCommonUtil.short2bigdecimal(roomHumidity, BigDecimal.valueOf(100)))
                    .roomTemperature(IotCommonUtil.short2bigdecimal(roomTemperature,BigDecimal.valueOf(100)))
                    .watt(IotCommonUtil.short2bigdecimal(watt,BigDecimal.valueOf(100)))
                    .build();
            roomDataVOList.add(roomDataVO);

        }
        return roomDataVOList;
    }


}
