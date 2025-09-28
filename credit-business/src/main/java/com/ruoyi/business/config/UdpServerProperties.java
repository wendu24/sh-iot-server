package com.ruoyi.business.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "udp")
@Data
public class UdpServerProperties {

    private int port = 9990;
    private int workerThreads = 4;
    private int businessThreads = 32;
    private int receiveBufferSize = 1024 * 1024;
    private int sendBufferSize = 1024 * 1024;
    private boolean reuseAddress = true;
    private boolean broadcast = false;


}
