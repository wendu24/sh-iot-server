package com.ruoyi.business.iot.protocol;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.protocol.vo.UplinkDataVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttSubcriber {


    public void subscribe(){

        String payload = "";
//        String hexString = "aa7233002069afb3c941c9d7904a2304db25099c760d09458ddb7328c8bc840cbfcd6049179e0a722b4ee07241c977a5db72e03a7fafdd";
//        String hexString = "aa724300206d884811c67021fa9046c4454afe3c4625e05cec5c29b78595922e2bc2430220ee5e4ff1eed3ca3db5c56a4f76b4b09e6435b1955f4bedd1e5437eeba40629c3e9dd";
        String hexString = "aa729300202563eb117a2f53cad8253be245f14e4625e05cec5c29b78595922e2bc2430220c60a38a703db1d6af14dd7d9d5e2b28b7dd0a5a9a672148f6159b0d7d995d3b90ee2fc0c7ca2e3ef8e751e637364163b872fb400a3d955cabadcbe1166e4860225e05cec5c29b78595922e2bc2430220f255e43390e41ca677f68bfe323b72272c6ea2ec588d5ca1925048117ab04a8f96dd";

        byte[] mockReceivedData = IotCommonUtil.hexToBytes(hexString);
        String deviceSN = "";
        String aesKey = AesUtil.getAesKey(deviceSN);
        // 2. 调用 Parser 解析接收到的数据
        try {
            byte[] decryptedBody = UplinkDataParser.parseReceivedData(mockReceivedData,aesKey);
            System.out.println("解密后的数据 decryptedBody : " + IotCommonUtil.bytesToHex(decryptedBody));
            UplinkDataVO uplinkDataVO = UplinkDataParser.parseData(decryptedBody);
            System.out.println(JSONObject.toJSONString(uplinkDataVO));
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
        }

    }


    public static void main(String[] args) {
        MqttSubcriber mqttSubcriber = new MqttSubcriber();
        mqttSubcriber.subscribe();
    }
}
