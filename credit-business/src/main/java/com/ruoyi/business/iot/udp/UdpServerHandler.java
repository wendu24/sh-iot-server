package com.ruoyi.business.iot.udp;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.vo.UplinkDataVO;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.parser.UdpDataParseContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    private final ExecutorService businessExecutor;
    private final UplinkMsgHandler uplinkMsgHandler;

    public UdpServerHandler(ExecutorService businessExecutor, UplinkMsgHandler uplinkMsgHandler) {
        this.businessExecutor = businessExecutor;
        this.uplinkMsgHandler = uplinkMsgHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] data = new byte[packet.content().readableBytes()];
        packet.content().readBytes(data);
        String msg = IotCommonUtil.bytesToHex(data);
        InetSocketAddress sender = packet.sender();
        log.info("sender={}", JSONObject.toJSONString(sender));
        // 解析SN，假设协议是 JSON，例如 {"sn":"ABC123","data":"xxx"}
        log.info("收到UDP请求 msg={}",msg);
        String sn = UdpDataParseContext.parseSn(msg);
        // 更新设备地址
        DeviceSessionManager.updateDevice(sn, sender);
        UplinkDataVO uplinkDataVO = UdpDataParseContext.parseData(sn, msg);
        log.info("解析出来的数据 headerDataVO={}",uplinkDataVO);
        uplinkMsgHandler.handle(uplinkDataVO);
        // 根据业务需要决定是否回复
//        String resp = "ACK:" + msg;
//        ByteBuf buf = Unpooled.copiedBuffer(resp, CharsetUtil.UTF_8);
//        ctx.writeAndFlush(new DatagramPacket(buf, sender));
    }

    private String parseSnFromMessage(String msg) {
        try {
            // 简单解析SN，可以换成Jackson/Gson解析JSON
            int start = msg.indexOf("\"sn\":\"");
            if (start > -1) {
                int end = msg.indexOf("\"", start + 6);
                return msg.substring(start + 6, end);
            }
        } catch (Exception ignored) {}
        return null;
    }
    private String processMessage(String msg) {
        // TODO: 自定义业务逻辑：解析协议、校验、存库、调用其它服务等
        log.info("udp接收到的消息{}",msg);
        // 下面只是示例回显
        return "ACK:" + msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 避免因单条消息异常影响 channel
        cause.printStackTrace();
    }
}
