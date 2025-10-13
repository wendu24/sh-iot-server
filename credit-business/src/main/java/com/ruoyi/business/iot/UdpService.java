package com.ruoyi.business.iot;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.handler.DownMsgHandler;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.packager.udp.UdpDataPackager;
import com.ruoyi.business.iot.parser.UdpDataParseContext;
import com.ruoyi.business.iot.udp.NettyUdpServer;
import com.ruoyi.business.vo.UdpManualDownVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UdpService {

    private static final ConcurrentHashMap<String, List<DtuDownDataVO>> dataCache = new ConcurrentHashMap<>();

    @Autowired
    private DownMsgHandler downMsgHandler;

    @Autowired
    private MidGenerator midGenerator;

    @Autowired
    private UplinkMsgHandler uplinkMsgHandler;

    @Autowired
    private NettyUdpServer nettyUdpServer;

    /**
     * 处理所有接收到的所有的udp数据
     *
     * @param msg 十六进制数据字符串
     */
    public void handleAllMsg(String msg) {
        String sn = UdpDataParseContext.parseSn(msg);
        /**
         * 数据解析
         */
        UplinkDataVO uplinkDataVO = UdpDataParseContext.parseData(sn, msg);
        log.info("解析出来的数据 headerDataVO={}", uplinkDataVO);
        /**
         * 数据处理
         */
        uplinkMsgHandler.handle(uplinkDataVO);
        /**
         * 接收到消息后,触发缓存消息下发
         */
        sendCachedMsg(sn);
    }


    /**
     * 由于UDP不稳定,所以先缓存,设备上传数据后在一次性下发
     *
     * @param sn
     * @param dtuDownDataVO
     * @throws Exception
     */
    public void sendCommand2cache(String sn, DtuDownDataVO dtuDownDataVO) {
        log.info("命令加入udp缓存={}", JSONObject.toJSONString(dtuDownDataVO));
        List<DtuDownDataVO> list = dataCache.computeIfAbsent(sn, k -> new ArrayList<>());
        list.add(dtuDownDataVO); // 直接操作返回的列表
    }

    /**
     * 直接下发
     *
     * @param sn
     * @param dtuDownDataVO
     * @throws Exception
     */
    @Async
    public void sendCommandAsync(String sn, DtuDownDataVO dtuDownDataVO) {
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
        dtuDownDataVO.setPublishTime(LocalDateTime.now());
        try {
            Thread.sleep(5000);
            /**
             * 构造数据下发的字节数组
             */
            byte[] dataBytes = UdpDataPackager.build(dtuDownDataVO, sn, AesUtil.getAesKey(sn));
            /**
             * 下发数据
             */
            nettyUdpServer.sendUdpMsg(sn, dataBytes);
            /**
             * 数据下发后,做对应的处理(比如保存记录)
             */
            downMsgHandler.handle(dtuDownDataVO);
        } catch (Exception e) {
            log.error("构建下发数据出错啦dtuDownDataVO={}", JSONObject.toJSONString(dtuDownDataVO), e);
        }
    }


    /**
     * 发送缓存的消息
     *
     * @param sn
     */
    public void sendCachedMsg(String sn) {
        List<DtuDownDataVO> dtuDownDataVOS = dataCache.get(sn);
        if (CollectionUtils.isEmpty(dtuDownDataVOS))
            return;
        dtuDownDataVOS.forEach(dtuDownDataVO -> {
            try {
                log.info("sn={}准备下发缓存的udp数据 dtuDownDataVO={}", sn, JSONObject.toJSONString(dtuDownDataVO));
                sendCommandAsync(sn, dtuDownDataVO);
            } catch (Exception e) {
                log.error("构建下发数据出错啦dtuDownDataVO={}", JSONObject.toJSONString(dtuDownDataVO), e);
            }
        });
        clearCacheData(sn);
    }

    /**
     * 手动发送消息
     *
     * @param udpManualDownVO
     */
    public void sendMsgManual(UdpManualDownVO udpManualDownVO) {
        udpManualDownVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
        udpManualDownVO.setPublishTime(LocalDateTime.now());
        String sn = udpManualDownVO.getDataVOList().get(0).getDeviceSn();
        DtuDownDataVO dtuDownDataVO = DtuDownDataVO.builder()
                .publishTime(udpManualDownVO.getPublishTime())
                .dataVOList(udpManualDownVO.getDataVOList())
                .build();
        /**
         * 构造数据下发的字节数组
         */
        try {
            byte[] dataBytes = UdpDataPackager.build(dtuDownDataVO, sn, AesUtil.getAesKey(sn));
            nettyUdpServer.sendMessage(udpManualDownVO.getIp(), udpManualDownVO.getPort(), sn,dataBytes);
        } catch (Exception e) {
            log.error("手动发送信息出错",e);
        }
    }

    public static void clearCacheData(String deviceSn) {
        List<DtuDownDataVO> dtuDownDataVOS = dataCache.get(deviceSn);
        if (CollectionUtils.isNotEmpty(dtuDownDataVOS)) {
            log.info("清除缓存 dtuDownDataVOS={}", JSONObject.toJSONString(dtuDownDataVOS));
            dtuDownDataVOS.clear();
        }

    }
}
