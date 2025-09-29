package com.ruoyi.business.iot.packager.mqtt;

import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.util.DateUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * dtu部分打包, 时间戳、命令条数 、校验和cs2
 */
public class DtuDataPackager {


    public static byte[] buildCommand(DtuDownDataVO dtuDownDataVO) throws Exception {
        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();
        int timestamp = (int)DateUtil.localDateTimeToTimestamp(dtuDownDataVO.getPublishTime())/1000;
        outputStream.write(IotCommonUtil.intToBytes(timestamp));
        outputStream.write((byte)dtuDownDataVO.getDataVOList().size());
        dtuDownDataVO.getDataVOList().forEach(oneData ->{
            try {
                byte[] command = CmdDataPackager.buildCommand(oneData);
                outputStream.write(command);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        byte[] dtuDataByte = outputStream.toByteArray();
        byte cs2 = IotCommonUtil.CS_Check(dtuDataByte,dtuDataByte.length);
        outputStream.write(cs2);
        return outputStream.toByteArray();
    }
}
