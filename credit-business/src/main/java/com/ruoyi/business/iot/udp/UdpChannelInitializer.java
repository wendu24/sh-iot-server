package com.ruoyi.business.iot.udp;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import java.util.concurrent.ExecutorService;
public class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {

    private final ExecutorService businessExecutor;

    public UdpChannelInitializer(ExecutorService businessExecutor) {
        this.businessExecutor = businessExecutor;
    }

    @Override
    protected void initChannel(DatagramChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        // 根据需要可以添加编解码器/日志/限流等 handler
        p.addLast(new UdpServerHandler(businessExecutor));
    }
}
