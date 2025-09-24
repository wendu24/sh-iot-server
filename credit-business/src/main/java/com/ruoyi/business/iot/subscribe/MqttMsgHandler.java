package com.ruoyi.business.iot.subscribe;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.AesUtil;
import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.vo.uplink.DtuDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmd08DataVO;
import com.ruoyi.business.iot.common.vo.uplink.UplinkCmdFFDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
@Component
public class MqttMsgHandler {

    @Autowired
    private ThreadPoolTaskExecutor mqttMessageExecutor;

    /**
     * 异步处理
     * @param topic
     * @param hexString
     */
    public void handleSync(String topic, String hexString){
        mqttMessageExecutor.execute(()->handle(topic,hexString));
    }

    public DtuDataVO handle(String topic, String hexString){
        String[] parts = topic.split("/");
        String deviceSN = parts[3];
        DtuDataVO dtuDataVO = parse(deviceSN, hexString);
        return dtuDataVO;
    }


    public DtuDataVO parse(String deviceSN, String hexString){

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
             * 第二步: 解析出DTU数据
             */
            DtuDataVO dtuDataVO = DtuDataParser.parse(buffer);
            /**
             * 第三步:解析CMD数据
             */
            parseData(buffer,dtuDataVO);
            log.info("解析出的数据 {}",JSONObject.toJSONString(dtuDataVO));
            return dtuDataVO;
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
    private static void parseData(ByteBuffer allDataBuffer ,DtuDataVO dtuDataVO){
        /**
         * 有几条数据
         */
        Integer cmdNum = dtuDataVO.getCmdNum();
        for (int i = 0; i < cmdNum; i++) {
            String deviceSn = getDeviceSnBYte(allDataBuffer);
            ByteBuffer oneDataBuffer = getDataBuffer(allDataBuffer);
            byte cmd = oneDataBuffer.get();
            if(CmdEnum.UPLINK_08.getCode().equals(cmd)){
                UplinkCmd08DataVO uplinkCmd08DataVO = Cmd08DataParser.parse(oneDataBuffer);
                uplinkCmd08DataVO.setDeviceSn(deviceSn);
                dtuDataVO.addCmd08DataVOS(uplinkCmd08DataVO);
                // 回复数据的cmd取决于下发时的cmd
            }else {
                UplinkCmdFFDataVO cmdFFDataVO = CmdFFDataParser.parse(oneDataBuffer,cmd);
                dtuDataVO.addCmdFFDataVOS(cmdFFDataVO);
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
        MqttMsgHandler mqttMsgHandler = new MqttMsgHandler();
//        mqttMsgHandler.handle2();
    }
}
