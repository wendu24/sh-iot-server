package com.ruoyi.business.iot.udp;

import com.ruoyi.business.config.UdpServerProperties;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * 接收和发送消息
 */
@Component
public class NettyUdpServer {

    private final UdpServerProperties props;
    private EventLoopGroup group;
    private Channel channel;
    private ExecutorService businessExecutor;

    public NettyUdpServer(UdpServerProperties props) {
        this.props = props;
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

        b.handler(new UdpChannelInitializer(businessExecutor));

        // 绑定端口（UDP bind）
        channel = b.bind(new InetSocketAddress(props.getPort())).sync().channel();
        System.out.println("UDP server started on port: " + props.getPort());
    }


    public void sendCommand(String sn, String command) {
        InetSocketAddress target = DeviceSessionManager.getDeviceAddress(sn);
        if (target == null) {
            System.err.println("设备[" + sn + "]不在线，无法下发");
            return;
        }
        if (channel == null || !channel.isActive()) {
            System.err.println("UDP channel未就绪，无法下发");
            return;
        }
        ByteBuf buf = Unpooled.copiedBuffer(command, CharsetUtil.UTF_8);
        channel.writeAndFlush(new DatagramPacket(buf, target));
        System.out.printf("下发命令给设备[%s]: %s%n", sn, command);
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
