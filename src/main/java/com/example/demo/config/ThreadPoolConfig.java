package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * Limited platform thread pool to simulate thread exhaustion
     * Under high load, this pool will be exhausted causing request queuing
     */
    @Bean(name = "limitedPlatformThreadPool")
    public Executor limitedPlatformThreadPoolExecutor() {
        return Executors.newFixedThreadPool(10); // Intentionally limited for demo
    }

    /**
     * Virtual thread executor - can handle much higher concurrency
     * Virtual threads are lightweight and don't cause platform thread exhaustion
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Standard platform thread pool for comparison
     */
    @Bean(name = "standardPlatformThreadPool")
    public Executor standardPlatformThreadPoolExecutor() {
        return Executors.newCachedThreadPool(); // Can grow as needed
    }
}