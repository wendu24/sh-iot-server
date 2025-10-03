package com.ruoyi.business.iot.handler.uplink;

import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.constant.CmdEnum;
import com.ruoyi.business.iot.common.constant.ReadWriteEnum;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.CommonDownDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.common.vo.uplink.UdpCmd08DataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UdpCheckTimeObserver extends AbstractUplinkMsgObserver{

    @Autowired
    UdpService udpService;

    private static final ConcurrentHashMap<String, LocalDateTime> timeMap = new ConcurrentHashMap();

    @Override
    public void handle(UplinkDataVO uplinkDataVO) {
        if(Objects.isNull(uplinkDataVO.getUdpCmd08DataVO()))
            return;
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        try {
            log.info("下发udp 时间校验数据 sn={}",udpCmd08DataVO.getDeviceSn());
            udpService.sendCommand(udpCmd08DataVO.getDeviceSn(),buildCommand(uplinkDataVO));
        } catch (Exception e) {
            log.error("发布udp时间校验出错啦={}",udpCmd08DataVO.getDeviceSn(),e);
        }

    }


    private static DtuDownDataVO buildCommand(UplinkDataVO uplinkDataVO) {
        UdpCmd08DataVO udpCmd08DataVO = uplinkDataVO.getUdpCmd08DataVO();
        CommonDownDataVO commonDownDataVO = CommonDownDataVO.builder()
                .cmdCode(CmdEnum.DOWNLINK_FF.getCode())
                .readWriteFlag(ReadWriteEnum.RESPONSE.getCode())
                .deviceSn(udpCmd08DataVO.getDeviceSn())
                .build();
        DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder()
                .dataVOList(Arrays.asList(commonDownDataVO))
                .build();
        return dtuDownDataVO;
    }
}
