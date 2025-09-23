package com.ruoyi.business.iot.protocol;

import com.ruoyi.business.iot.protocol.constant.CmdEnum;
import com.ruoyi.business.iot.protocol.constant.ReadWriteEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class MqttPublisher {


    private final static Integer ONE = 1;

    /**
     * 发送读消息，单条
     * @param deviceSn 设备SN
     * @param cmdEnum 命令字
     * @return
     */
    public static boolean publishOneReadMsg(String deviceSn,
                                  CmdEnum cmdEnum
                                  ){
        try {
            // 第一步：构建消息体
            byte commandCount = ONE.byteValue();
            Short mid = MidGenerator.generatorMid(deviceSn);
            String aesKey = AesUtil.getAesKey(deviceSn);
            byte[] commandBody = DownlinkDataPackager.buildShortCommand(cmdEnum, mid, ReadWriteEnum.READ, null);
            // 第二步：发送消息
            int timestamp = (int) (new Date().getTime() / 1000);
            byte[] sn = IotCommonUtil.hexToBytes(deviceSn); // 有时候是设备的SN，有时候是DTU的SN
            DownlinkDataPackager.buildDownlinkMessage(timestamp,commandCount,sn,commandBody,aesKey);
            return true;
        } catch (Exception e) {
            log.error("发送消息出错啦！deviceSn={}",deviceSn,e);
            return false;
        }
    }

    /**
     * 发送写消息，单条
     * @param deviceSn 设备SN
     * @param cmdEnum 命令字
     * @param data 下发的数据
     * @return
     */
    public static boolean publishOneWriteMsg(
            String deviceSn,
            CmdEnum cmdEnum,
            short data
    ){
        try {
            // 第一步：构建消息体
            byte commandCount = ONE.byteValue();
            Short mid = MidGenerator.generatorMid(deviceSn);
            String aesKey = AesUtil.getAesKey(deviceSn);
            byte[] commandBody = DownlinkDataPackager.buildShortCommand(cmdEnum, mid, ReadWriteEnum.WRITE, data);
            // 第二步：发送消息
            int timestamp = (int) (new Date().getTime() / 1000);
            byte[] sn = IotCommonUtil.hexToBytes(deviceSn); // 有时候是设备的SN，有时候是DTU的SN
            DownlinkDataPackager.buildDownlinkMessage(timestamp,commandCount,sn,commandBody,aesKey);
            return true;
        } catch (Exception e) {
            log.error("发送消息出错啦！deviceSn={}",deviceSn,e);
            return false;
        }
    }


}
