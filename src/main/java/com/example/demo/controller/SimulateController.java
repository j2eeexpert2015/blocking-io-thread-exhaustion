package com.example.demo.controller;

import com.example.demo.service.SimulateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class SimulateController {

    private final SimulateService simulateService;

    public SimulateController(SimulateService simulateService) {
        this.simulateService = simulateService;
    }

    /**
     * Blocking I/O simulation using platform thread
     * Under high load, this will cause thread pool exhaustion
     */
    @GetMapping("/simulate-blocking")
    public ResponseEntity<String> simulateBlocking() {
        long startTime = System.currentTimeMillis();
        
        simulateService.simulateBlockingIO();
        
        long endTime = System.currentTimeMillis();
        String response = String.format("Blocking task completed in %d ms on thread: %s", 
                                      (endTime - startTime), Thread.currentThread().getName());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Virtual thread simulation
     * Can handle much higher concurrency without platform thread exhaustion
     */
    @GetMapping("/simulate-virtual")
    public ResponseEntity<String> simulateVirtual() {
        long startTime = System.currentTimeMillis();
        
        simulateService.simulateWithVirtualThread();
        
        long endTime = System.currentTimeMillis();
        String response = String.format("Virtual thread task completed in %d ms on thread: %s", 
                                      (endTime - startTime), Thread.currentThread().getName());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Async simulation using limited platform thread pool
     * Will demonstrate thread pool exhaustion under load
     */
    @GetMapping("/simulate-async-platform")
    public ResponseEntity<String> simulateAsyncPlatform() {
        long startTime = System.currentTimeMillis();
        
        try {
            CompletableFuture<String> future = simulateService.simulateAsyncPlatformThread();
            String result = future.get(); // Wait for completion
            
            long endTime = System.currentTimeMillis();
            String response = String.format("%s in %d ms", result, (endTime - startTime));
            
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError()
                    .body("Async platform thread task failed: " + e.getMessage());
        }
    }

    /**
     * Async simulation using virtual thread executor
     * Can handle high concurrency efficiently
     */
    @GetMapping("/simulate-async-virtual")
    public ResponseEntity<String> simulateAsyncVirtual() {
        long startTime = System.currentTimeMillis();
        
        try {
            CompletableFuture<String> future = simulateService.simulateAsyncVirtualThread();
            String result = future.get(); // Wait for completion
            
            long endTime = System.currentTimeMillis();
            String response = String.format("%s in %d ms", result, (endTime - startTime));
            
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError()
                    .body("Async virtual thread task failed: " + e.getMessage());
        }
    }

    /**
     * CPU-intensive task simulation
     * Shows that virtual threads are not beneficial for CPU-bound operations
     */
    @GetMapping("/simulate-cpu-intensive")
    public ResponseEntity<String> simulateCpuIntensive() {
        long startTime = System.currentTimeMillis();
        
        simulateService.simulateCpuIntensiveTask();
        
        long endTime = System.currentTimeMillis();
        String response = String.format("CPU-intensive task completed in %d ms on thread: %s", 
                                      (endTime - startTime), Thread.currentThread().getName());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Application is running. Current thread: " + Thread.currentThread().getName());
    }
}