package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(name = "limitedThreadPool")
    public Executor limitedThreadPoolExecutor() {
        return Executors.newFixedThreadPool(10); // Simulate platform thread exhaustion
    }
}
