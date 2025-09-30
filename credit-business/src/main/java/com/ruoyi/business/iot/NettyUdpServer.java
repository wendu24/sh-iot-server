package com.ruoyi.business.iot;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public static final ConcurrentHashMap<String, List<DtuDownDataVO>> dataCache = new ConcurrentHashMap<>();
    private final UdpServerProperties props;
    private final UplinkMsgHandler uplinkMsgHandler;
    private final DownMsgHandler downMsgHandler;
    private EventLoopGroup group;
    private Channel channel;
    private ExecutorService businessExecutor;
    private final MidGenerator midGenerator;

    public NettyUdpServer(UdpServerProperties props, UplinkMsgHandler uplinkMsgHandler, DownMsgHandler downMsgHandler,MidGenerator midGenerator) {
        this.props = props;
        this.uplinkMsgHandler = uplinkMsgHandler;
        this.downMsgHandler = downMsgHandler;
        this.midGenerator = midGenerator;
    }

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

        b.handler(new UdpChannelInitializer(businessExecutor,uplinkMsgHandler,this));

        // 绑定端口（UDP bind）
        channel = b.bind(new InetSocketAddress(props.getPort())).sync().channel();
        log.info("UDP server started on port: ={}" ,props.getPort());
    }

    /**
     * 由于UDP不稳定,所以先缓存,设备上传数据后在一次性下发
     * @param sn
     * @param dtuDownDataVO
     * @throws Exception
     */
    public void sendCommand(String sn, DtuDownDataVO dtuDownDataVO) throws Exception {
        List<DtuDownDataVO> list = dataCache.computeIfAbsent(sn, k -> new ArrayList<>());
        list.add(dtuDownDataVO); // 直接操作返回的列表
    }


    public void doSend(String sn)  {
        List<DtuDownDataVO> dtuDownDataVOS = dataCache.get(sn);
        if(CollectionUtils.isEmpty(dtuDownDataVOS))
            return;
        dtuDownDataVOS.forEach(dtuDownDataVO -> {
            dtuDownDataVO.getDataVOList().forEach(commonDownDataVO -> commonDownDataVO.setMid(midGenerator.generatorMid(commonDownDataVO.getDeviceSn())));
            dtuDownDataVO.setPublishTime(LocalDateTime.now());
            try {
                byte[] dataBytes = UdpDataPackager.build(dtuDownDataVO, sn, AesUtil.getAesKey(sn));
                InetSocketAddress target = DeviceSessionManager.getDeviceAddress(sn);
                if (target == null) {
                    log.error("设备={}不在线,无法下发",sn);
                    return;
                }
                if (channel == null || !channel.isActive()) {
                    log.error("UDP channel未就绪，无法下发");
                    return;
                }
                log.info("udp下发消息target={}", JSONObject.toJSONString(target));
                ByteBuf byteBuf = Unpooled.wrappedBuffer(dataBytes);
                channel.writeAndFlush(new DatagramPacket(byteBuf, target));
                downMsgHandler.handle(dtuDownDataVO);
            } catch (Exception e) {
                log.error("构建下发数据出错啦dtuDownDataVO={}",JSONObject.toJSONString(dtuDownDataVO),e);
            }
        });
        dtuDownDataVOS.clear();
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

    public static void main(String[] args) {
        String hex = "3132312E34332E3137392E3234353A39393930006F6D";
        String hex2 = "3132312E34332E3137392E3234353A39393930";
        byte[] bytes = IotCommonUtil.hexToBytes(hex);
        byte[] bytes2 = IotCommonUtil.hexToBytes(hex2);
        System.out.println(new String(bytes));
        System.out.println(new String(bytes2));
    }
}
