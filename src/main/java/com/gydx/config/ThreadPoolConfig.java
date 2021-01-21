package com.gydx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 拽小白
 * @createTime 2020-11-01 13:54
 * @description
 */
@Configuration
public class ThreadPoolConfig {

    @Value("${threadPool.corePoolSize}")
    private Integer corePoolSize;
    @Value("${threadPool.maxPoolSize}")
    private Integer maxPoolSize;
    @Value("${threadPool.queueCapacity}")
    private Integer queueCapacity;
    @Value("${threadPool.keepAliveSeconds}")
    private Integer keepAliveSeconds;
    @Value("${threadPool.namePrefix}")
    private String threadNamePrefix;

    @Bean("threadPoolTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
