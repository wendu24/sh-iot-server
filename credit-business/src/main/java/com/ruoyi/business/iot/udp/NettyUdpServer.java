package com.ruoyi.business.iot.udp;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.business.config.UdpServerProperties;
import com.ruoyi.business.iot.common.util.AesUtil;
import com.ruoyi.business.iot.common.util.IotCommonUtil;
import com.ruoyi.business.iot.common.util.MidGenerator;
import com.ruoyi.business.iot.common.vo.down.DtuDownDataVO;
import com.ruoyi.business.iot.handler.DownMsgHandler;
import com.ruoyi.business.iot.handler.UplinkMsgHandler;
import com.ruoyi.business.iot.packager.udp.UdpDataPackager;
import com.ruoyi.business.iot.udp.DeviceSessionManager;
import com.ruoyi.business.iot.udp.UdpChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;

/**
 * 接收和发送消息
 */
@Slf4j
@Component
public class NettyUdpServer {

    @Autowired
    private UdpServerProperties props;

    @Autowired
    private UdpChannelInitializer udpChannelInitializer;
    private EventLoopGroup group;
    private Channel channel;

    private ExecutorService businessExecutor;



    @PostConstruct
    public void start() throws InterruptedException {
        // 业务线程池
        businessExecutor = Executors.newFixedThreadPool(props.getBusinessThreads(), new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "udp-business-" + idx.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });

        boolean useEpoll = Epoll.isAvailable(); // Linux native epoll
        if (useEpoll) {
            group = new EpollEventLoopGroup(props.getWorkerThreads());
        } else {
            group = new NioEventLoopGroup(props.getWorkerThreads());
        }

        Bootstrap b = new Bootstrap();
        b.group(group)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_RCVBUF, props.getReceiveBufferSize())
                .option(ChannelOption.SO_SNDBUF, props.getSendBufferSize())
                .option(ChannelOption.SO_REUSEADDR, props.isReuseAddress())
                .option(ChannelOption.SO_BROADCAST, props.isBroadcast());

        if (useEpoll) {
            b.channel(EpollDatagramChannel.class);
        } else {
            b.channel(NioDatagramChannel.class);
        }

        b.handler(udpChannelInitializer);

        // 绑定端口（UDP bind）
        channel = b.bind(new InetSocketAddress(props.getPort())).sync().channel();
        log.info("UDP server started on port: ={}", props.getPort());
    }


    /**
     * 手动发送消息
     * @param ip
     * @param port
     * @param dataBytes
     */
    public void sendMessage(String ip, int port,String deviceSn, byte[] dataBytes) {
        InetSocketAddress target = new InetSocketAddress(ip, port);
//        ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(dataBytes);
        DatagramPacket packet = new DatagramPacket(byteBuf, target);
        log.info("udp下发deviceSn={}消息target={}",deviceSn, JSONObject.toJSONString(target));
        channel.writeAndFlush(packet);
    }



    public void sendUdpMsg(String deviceSn, byte[] dataBytes){
        InetSocketAddress target = DeviceSessionManager.getDeviceAddress(deviceSn);
        if (target == null) {
            log.error("设备={}不在线,无法下发", deviceSn);
            return;
        }
        if (channel == null || !channel.isActive()) {
            log.error("UDP channel未就绪，无法下发");
            return;
        }
        log.info("udp下发deviceSn={}消息target={}",deviceSn, JSONObject.toJSONString(target));
        ByteBuf byteBuf = Unpooled.wrappedBuffer(dataBytes);
        channel.writeAndFlush(new DatagramPacket(byteBuf, target));
    }

    @PreDestroy
    public void stop() {
        try {
            if (channel != null) {
                channel.close().syncUninterruptibly();
            }
        } finally {
            if (group != null) {
                group.shutdownGracefully();
            }
            if (businessExecutor != null) {
                businessExecutor.shutdownNow();
            }
        }
        System.out.println("UDP server stopped.");
    }


}
