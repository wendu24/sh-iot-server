package com.ruoyi.business.iot.parser;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.constant.UplinkCmdEnum;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.MqttCmd08DataVO;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.iot.parser.mqtt.Cmd08DataParser;
import com.ruoyi.business.iot.parser.mqtt.CmdFFDataParser;
import com.ruoyi.business.iot.parser.mqtt.DtuDataParser;
import com.ruoyi.business.iot.parser.mqtt.OuterDataParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class MqttDataParseContext {


    public static UplinkDataVO parse(String topic, String hexString){
        String[] parts = topic.split("/");
        String dtuDeviceSN = parts[3];
        UplinkDataVO uplinkDataVO = parseData(dtuDeviceSN, hexString);
        return uplinkDataVO;
    }


    private static UplinkDataVO parseData(String dtuDeviceSN, String hexString){

        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        String aesKey = AesUtil.getAesKey(dtuDeviceSN);
        try {
            /**
             * 第一步: 解析出消息体并解密
             */
            byte[] decryptedBody = OuterDataParser.parse(rawData,aesKey);
            System.out.println("解密后的数据 decryptedBody : " + IotCommonUtil.bytesToHex(decryptedBody));
            // 这个buffer整个链路一直在用
            ByteBuffer buffer = ByteBuffer.wrap(decryptedBody).order(ByteOrder.LITTLE_ENDIAN);
            /**
             * 第二步: 解析出DTU数据
             */
            UplinkDataVO uplinkDataVO = DtuDataParser.parse(dtuDeviceSN, buffer);
            /**
             * 第三步:解析CMD数据
             */
            parseData(buffer, uplinkDataVO);
            log.info("解析出的数据 {}",JSONObject.toJSONString(uplinkDataVO));
            return uplinkDataVO;
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
            return null;
        }
    }



    /**
     *  解析业务数据, 通过cmd判断是CMD08 还是reply数据
     * @param allDataBuffer
     * @return
     */
    private static void parseData(ByteBuffer allDataBuffer , UplinkDataVO uplinkDataVO){
        /**
         * 有几条数据
         */
        Integer cmdNum = uplinkDataVO.getDtuDataVO().getCmdNum();
        for (int i = 0; i < cmdNum; i++) {
            String deviceSn = getDeviceSnBYte(allDataBuffer);
            ByteBuffer oneDataBuffer = getDataBuffer(allDataBuffer);
            byte cmd = oneDataBuffer.get();
            if(UplinkCmdEnum.UPLINK_08.getCode().equals(cmd)){
                MqttCmd08DataVO mqttCmd08DataVO = Cmd08DataParser.parse(oneDataBuffer);
                mqttCmd08DataVO.setDeviceSn(deviceSn);
                uplinkDataVO.addCmd08DataVOS(mqttCmd08DataVO);
                // 回复数据的cmd取决于下发时的cmd
            }else {
                CmdFFDataVO cmdFFDataVO = CmdFFDataParser.parse(oneDataBuffer,cmd);
                cmdFFDataVO.setDeviceSn(deviceSn);
                uplinkDataVO.addCmdFFDataVOS(cmdFFDataVO);
            }
        }
    }


    private static ByteBuffer getDataBuffer(ByteBuffer buffer) {
        short dataLength = buffer.getShort();
        byte[] dataBytes = new byte[dataLength];
        buffer.get(dataBytes);
        ByteBuffer dataBuffer = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN);
        return dataBuffer;
    }

    private static String getDeviceSnBYte(ByteBuffer buffer) {
        byte snLength = buffer.get();
        byte[] snBytes = new byte[snLength];
        buffer.get(snBytes);
        return IotCommonUtil.bytesToHex(snBytes);
    }

    public static void main(String[] args) {
        MqttDataParseContext mqttDataParseContext = new MqttDataParseContext();
//        mqttMsgHandler.handle2();
    }
}
