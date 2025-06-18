package com.example.demo.controller;

import com.example.demo.service.SimulateService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Timed(value = "simulate_blockingio_execution", description = "Time taken for blocking I/O operations")
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
    @Timed(value = "simulate_virtual_execution", description = "Time taken for virtual thread operations")
    public ResponseEntity<String> simulateVirtual() {
        long startTime = System.currentTimeMillis();
        
        simulateService.simulateWithVirtualThread();
        
        long endTime = System.currentTimeMillis();
        String response = String.format("Virtual thread task completed in %d ms on thread: %s", 
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