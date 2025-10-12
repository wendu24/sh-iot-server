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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        log.info("命令加入udp缓存={}",JSONObject.toJSONString(dtuDownDataVO));
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
    public void sendCommand(String sn, DtuDownDataVO dtuDownDataVO) throws Exception {
        dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
        dtuDownDataVO.setPublishTime(LocalDateTime.now());
        try {
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
     * @param sn
     */
    public void sendCachedMsg(String sn) {
        List<DtuDownDataVO> dtuDownDataVOS = dataCache.get(sn);
        if (CollectionUtils.isEmpty(dtuDownDataVOS))
            return;
        dtuDownDataVOS.forEach(dtuDownDataVO -> {
            try {
                log.info("sn={}准备下发缓存的udp数据 dtuDownDataVO={}",sn,JSONObject.toJSONString(dtuDownDataVO));
                sendCommand(sn, dtuDownDataVO);
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("构建下发数据出错啦dtuDownDataVO={}", JSONObject.toJSONString(dtuDownDataVO), e);
            }
        });
        dtuDownDataVOS.clear();
    }

}
