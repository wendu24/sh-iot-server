package com.ruoyi.business.iot.parser;

import com.ruoyi.business.iot.common.constant.DownCmdEnum;
import com.ruoyi.business.iot.common.constant.UplinkCmdEnum;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.uplink.CmdFFDataVO;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import com.ruoyi.business.iot.parser.udp.Cmd08DataParser;
import com.ruoyi.business.iot.parser.udp.CmdFFDataParser;
import com.ruoyi.business.iot.parser.udp.OuterDataParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Slf4j
public class UdpDataParseContext {

    /**
     * 解析SN
     * @param hexString
     * @return
     */
    public static String parseSn( String hexString){
        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        byte snLength = rawData[5];
        byte[] snByte = Arrays.copyOfRange(rawData, 6, 6+snLength);
       return IotCommonUtil.bytesToHex(snByte);
    }


    /**
     * 解析数据
     * @param deviceSn
     * @param hexString
     * @return
     */
    public static UplinkDataVO parseData(String deviceSn, String hexString,String aesKey){

        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        String version = parseVersion(hexString);
        try {
            /**
             * 第一步: 解析出消息体并解密
             */
            byte[] decryptedBody = OuterDataParser.parse(rawData,aesKey);
            log.info("解密后的数据 decryptedBody :{}",IotCommonUtil.bytesToHex(decryptedBody));
            // 这个buffer整个链路一直在用
            ByteBuffer buffer = ByteBuffer.wrap(decryptedBody).order(ByteOrder.LITTLE_ENDIAN);

            /**
             * 第三步:解析CMD数据
             */
            return parseData(buffer,version);
        } catch (Exception e) {
            log.error("消息解析出错啦hexString={}",hexString,e);
            return null;
        }
    }


    private static UplinkDataVO parseData(ByteBuffer buffer,String version) {
        byte snLength = buffer.get();
        byte[] snByte = new byte[snLength];
        buffer.get(snByte);
        short dataLength = buffer.getShort();
        byte cmdCode = buffer.get();
        String deviceSn = IotCommonUtil.bytesToHex(snByte);
        UplinkDataVO uplinkDataVO = UplinkDataVO.builder()
                .build();
        if(UplinkCmdEnum.UPLINK_08.getCode().equals(cmdCode)){
            parseCmd08(buffer, deviceSn, uplinkDataVO,version);
        }else {
            CmdFFDataVO cmdFFDataVO = CmdFFDataParser.parse(buffer, cmdCode);
            cmdFFDataVO.setDeviceSn(deviceSn);
            uplinkDataVO.setCmdFFDataVOS(Arrays.asList(cmdFFDataVO));
        }
        return uplinkDataVO;
    }

    private static void parseCmd08(ByteBuffer buffer, String deviceSn, UplinkDataVO uplinkDataVO,String version) {
        if("10".equalsIgnoreCase(version)){
            UdpCmd08DataVO udpCmd08DataVO = Cmd08DataParser.parse(deviceSn, buffer);
            uplinkDataVO.setUdpCmd08DataVO(udpCmd08DataVO);
        }else {
            UdpCmd08DataVO udpCmd08DataVO = com.ruoyi.business.iot.parser.udp.v11.Cmd08DataParser.parse(deviceSn, buffer);
            uplinkDataVO.setUdpCmd08DataVO(udpCmd08DataVO);
        }
    }



    /**
     * 解析SN
     * @param hexString
     * @return
     */
    public static String parseVersion( String hexString){
        byte[] rawData = IotCommonUtil.hexToBytes(hexString);
        byte rawDatum = rawData[4];
        return IotCommonUtil.byteToHex(rawDatum);
    }


}
