package com.ruoyi.business.iot.subscribe;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.AesUtil;
import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.vo.DtuDataVO;
import com.ruoyi.business.iot.common.vo.UplinkCmd08DataVO;
import com.ruoyi.business.iot.common.vo.UplinkCmdFFDataVO;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class MqttSubcriber {


    public void handle(){

        String payload = "";
        // reply信息
        String hexString = "aa723300209d91b90d8898329f827d450d2e56e2abfd1a4d2ba8d5b1220b7a31c201fa44d87b498119fec4deee619dac3542c190ed6fdd";
//        String hexString = "aa729300202563eb117a2f53cad8253be245f14e4625e05cec5c29b78595922e2bc2430220c60a38a703db1d6af14dd7d9d5e2b28b7dd0a5a9a672148f6159b0d7d995d3b90ee2fc0c7ca2e3ef8e751e637364163b872fb400a3d955cabadcbe1166e4860225e05cec5c29b78595922e2bc2430220f255e43390e41ca677f68bfe323b72272c6ea2ec588d5ca1925048117ab04a8f96dd";

        byte[] mockReceivedData = IotCommonUtil.hexToBytes(hexString);
        String deviceSN = "";
        String aesKey = AesUtil.getAesKey(deviceSN);
        try {
            /**
             * 第一步: 解析出消息体并解密
             */
            byte[] decryptedBody = RawDataParser.parse(mockReceivedData,aesKey);
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
        } catch (Exception e) {
            log.error("消息解析出错啦",e);
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
                UplinkCmdFFDataVO cmdFFDataVO = CmdFFDataParser.parse(oneDataBuffer);
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
        MqttSubcriber mqttSubcriber = new MqttSubcriber();
        mqttSubcriber.handle();
    }
}
