package com.example.demo.service;

import io.micrometer.core.annotation.Timed;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SimulateService {

    /**
     * Simulate blocking I/O operation using current thread (platform thread)
     * This will block the calling thread for the entire duration
     */
    @Timed(value = "simulate_blockingio_execution", description = "Time taken to execute blocking I/O on platform thread")
    public void simulateBlockingIO() {
        try {
            // Simulate variable I/O latency (1-3 seconds)
            int delay = ThreadLocalRandom.current().nextInt(1000, 3001);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Blocking I/O was interrupted", e);
        }
    }

    /**
     * Simulate I/O operation using virtual thread
     * The virtual thread will not block the platform thread pool
     */
    @Timed(value = "simulate_virtual_execution", description = "Time taken to execute I/O with virtual thread")
    public void simulateWithVirtualThread() {
        try {
            Thread virtualThread = Thread.startVirtualThread(() -> {
                try {
                    // Simulate variable I/O latency (1-3 seconds)
                    int delay = ThreadLocalRandom.current().nextInt(1000, 3001);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            virtualThread.join(); // Wait for completion
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Virtual thread execution was interrupted", e);
        }
    }

    /**
     * Async version using limited platform thread pool
     * This will demonstrate thread pool exhaustion under load
     */
    @Async("limitedPlatformThreadPool")
    @Timed(value = "simulate_async_platform_execution", description = "Time taken for async execution on limited platform thread pool")
    public CompletableFuture<String> simulateAsyncPlatformThread() {
        try {
            int delay = ThreadLocalRandom.current().nextInt(1000, 3001);
            Thread.sleep(delay);
            return CompletableFuture.completedFuture("Platform thread task completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(new RuntimeException("Async platform thread was interrupted", e));
        }
    }

    /**
     * Async version using virtual thread executor
     * This can handle much higher concurrency without thread exhaustion
     */
    @Async("virtualThreadExecutor")
    @Timed(value = "simulate_async_virtual_execution", description = "Time taken for async execution on virtual thread")
    public CompletableFuture<String> simulateAsyncVirtualThread() {
        try {
            int delay = ThreadLocalRandom.current().nextInt(1000, 3001);
            Thread.sleep(delay);
            return CompletableFuture.completedFuture("Virtual thread task completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(new RuntimeException("Async virtual thread was interrupted", e));
        }
    }

    /**
     * CPU-intensive task to demonstrate that virtual threads are not suitable for CPU-bound operations
     */
    @Timed(value = "simulate_cpu_intensive_execution", description = "Time taken for CPU-intensive task")
    public void simulateCpuIntensiveTask() {
        // Simulate CPU-bound work (not suitable for virtual threads)
        long result = 0;
        for (int i = 0; i < 1_000_000; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }
        // Ensure the result is used to prevent optimization
        if (result < 0) {
            System.out.println("Unexpected result: " + result);
        }
    }
}