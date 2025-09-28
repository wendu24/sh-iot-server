package com.ruoyi.business.iot.parser.udp;

import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.room.DeviceDataVO;
import com.ruoyi.business.iot.parser.RawDataParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class UdpDataParser {


    public static DeviceDataVO parseData(String deviceSN, String hexString){

        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        String aesKey = AesUtil.getAesKey(deviceSN);
        try {
            /**
             * 第一步: 解析出消息体并解密
             */
            byte[] decryptedBody = RawDataParser.parse(rawData,aesKey);
            System.out.println("解密后的数据 decryptedBody : " + IotCommonUtil.bytesToHex(decryptedBody));
            // 这个buffer整个链路一直在用
            ByteBuffer buffer = ByteBuffer.wrap(decryptedBody).order(ByteOrder.LITTLE_ENDIAN);

            /**
             * 第三步:解析CMD数据
             */
            return Cmd08DataParser.parse(buffer);
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
            return null;
        }
    }

}
