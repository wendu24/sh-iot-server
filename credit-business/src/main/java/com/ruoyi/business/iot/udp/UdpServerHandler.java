package com.ruoyi.business.iot.udp;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.UdpService;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.parser.UdpDataParseContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    @Autowired UdpService udpService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] data = new byte[packet.content().readableBytes()];
        packet.content().readBytes(data);
        String msg = IotCommonUtil.bytesToHex(data);
        InetSocketAddress sender = packet.sender();
        // 解析SN，假设协议是 JSON，例如 {"sn":"ABC123","data":"xxx"}
        log.info("收到UDP ={} 请求 msg={}",JSONObject.toJSONString(sender),msg);
        String sn = UdpDataParseContext.parseSn(msg);
//        // 更新设备地址
        DeviceSessionManager.updateDevice(sn, sender);

        udpService.handleAllMsg(msg);

//        UplinkDataVO uplinkDataVO = UdpDataParseContext.parseData(sn, msg);
//        log.info("解析出来的数据 headerDataVO={}",uplinkDataVO);
//        uplinkMsgHandler.handle(uplinkDataVO);
//        /**
//         * 触发消息下发
//         */
//        nettyUdpServer.doSend(sn);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 避免因单条消息异常影响 channel
        log.error("udp报错",cause);
    }
}
