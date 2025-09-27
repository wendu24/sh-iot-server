package com.ruoyi.business.iot.publish;

import com.ruoyi.business.iot.common.AesUtil;
import com.ruoyi.business.iot.common.IotCommonUtil;
import com.ruoyi.business.iot.common.MidGenerator;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Deprecated
public class MqttPublisher {


    private final static Integer ONE = 1;

    /**
     * 发送读消息，单条
     * @param deviceSn 设备SN
     * @param cmdEnum 命令字
     * @return
     */
    @Deprecated
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
            byte[] bytes = DownlinkDataPackager.buildDownlinkMessage(timestamp, commandCount, sn, commandBody, aesKey);
            log.info("消息 {}", IotCommonUtil.bytesToHex(bytes));
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
    @Deprecated
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


//    public static void main(String[] args) throws Exception {
//
//        String deviceSn = "105110042509083201";
//        CmdEnum downlink25 = CmdEnum.DOWNLINK_25;
//
//        publishOneReadMsg(deviceSn,downlink25);
//
//        CommonDownDataVO commonDownDataVO = CommonDownDataVO.builder()
//                .mid(MidGenerator.generatorMid(deviceSn))
//                .cmdEnum(CmdEnum.DOWNLINK_25)
//                .deviceSn(deviceSn)
//                .readWriteFlag(ReadWriteEnum.READ)
//                .build();
//        DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder()
//                .publishTime(LocalDateTime.now())
//                .dataVOList(Arrays.asList(commonDownDataVO))
//                .build();
//        CompleteDataPackager.build(dtuDownDataVO,AesUtil.getAesKey(deviceSn));
//
//
//    }

}
