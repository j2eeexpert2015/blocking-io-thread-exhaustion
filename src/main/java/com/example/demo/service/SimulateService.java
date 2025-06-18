package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class SimulateService {

    // Constant delay for consistent testing
    private static final int IO_DELAY_MS = 2000; // 2 seconds

    /**
     * Simulate blocking I/O operation using current thread (platform thread)
     * This will block the calling thread for the entire duration
     */
    public void simulateBlockingIO() {
        try {
            Thread.sleep(IO_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Blocking I/O was interrupted", e);
        }
    }

    /**
     * Simulate I/O operation - when called on virtual thread, it will be efficient
     * When called on platform thread, it will block
     * The difference comes from the thread pool configuration, not the method itself
     */
    public void simulateWithVirtualThread() {
        try {
            Thread.sleep(IO_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("I/O operation was interrupted", e);
        }
    }
}