package com.ruoyi.business.iot.parser.udp;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.room.DeviceDataVO;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Slf4j
public class UdpDataParser {

    public static void main(String[] args) {
        String hex = "AA723D00100910211004250908000151F8696B734AFDFAF7B3CE47E96771E6FB9D6D242B73E0D69C7A42B1298E803C56E2522A8C5A6046B78B956AF533771654DD";
        DeviceDataVO deviceDataVO = parseData(parseSn(hex),hex);
        System.out.println(JSONObject.toJSONString(deviceDataVO));
    }


    public static String parseSn( String hexString){
        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        byte snLength = rawData[5];
        byte[] snByte = Arrays.copyOfRange(rawData, 6, 6+snLength);
       return IotCommonUtil.bytesToHex(snByte);
    }
    public static DeviceDataVO parseData(String deviceSn, String hexString){

        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
//        byte snLength = rawData[5];
//        byte[] snByte = Arrays.copyOfRange(rawData, 6, 6+snLength);
//        String deviceSn = IotCommonUtil.bytesToHex(snByte);
        String aesKey = AesUtil.getAesKey(deviceSn);
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
            return HeaderParser.parse(buffer);
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
            return null;
        }
    }

}
