package com.ruoyi.business.iot.subscribe;

import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.DtuDataVO;

import java.nio.ByteBuffer;

public class DtuDataParser {


    /**
        解析出DTU 的数据
     * @param buffer 解密后的协议体字节数组（即原始报文去除加密层和校验字段后的内容）
     */
    public static DtuDataVO parse(ByteBuffer buffer) {

        byte batteryLevel = buffer.get();        // 电池电量（单位：百分比 %）
        byte signalStrength = buffer.get();        // 信号强度
        byte[] iccIDBytes = new byte[11];
        buffer.get(iccIDBytes);
        byte replyFlag = buffer.get();
        byte cmdNum = buffer.get();

        return DtuDataVO.builder()
                .batteryLevel(IotCommonUtil.byte2int(batteryLevel))
                .signalStrength(IotCommonUtil.byte2int(signalStrength))
                .iccID(IotCommonUtil.bytesToHex(iccIDBytes))
                .replyFlag(IotCommonUtil.byte2int(replyFlag))
                .cmdNum(IotCommonUtil.byte2int(cmdNum))
                .build()
                ;
    }



}
