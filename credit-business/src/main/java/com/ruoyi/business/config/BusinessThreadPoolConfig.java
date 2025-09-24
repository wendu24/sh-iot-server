package com.ruoyi.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class BusinessThreadPoolConfig {

    @Bean(name ="mqttMessageExecutor")
    public ThreadPoolTaskExecutor mqttMessageExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数，保持线程活跃
        executor.setCorePoolSize(10);
        // 设置最大线程数，应对突发流量
        executor.setMaxPoolSize(50);
        // 设置队列容量，用于缓冲消息
        executor.setQueueCapacity(200);
        // 设置线程名称前缀，便于日志追踪
        executor.setThreadNamePrefix("mqtt-task-");
        // 设置当队列和线程池都满时，拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
