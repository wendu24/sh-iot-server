package com.ruoyi.business.iot.protocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttSubcriber {


    public void subscribe(){

        String payload = "";
        String hexString = "aa7233002069afb3c941c9d7904a2304db25099c760d09458ddb7328c8bc840cbfcd6049179e0a722b4ee07241c977a5db72e03a7fafdd";

        byte[] mockReceivedData = IotCommonUtil.hexToBytes(hexString);
        String deviceSN = "";
        String aesKey = AesUtil.getAesKey(deviceSN);
        // 2. 调用 Parser 解析接收到的数据
        try {
            byte[] decryptedBody = UplinkDataParser.parseReceivedData(mockReceivedData,aesKey);
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
        }

    }
}
