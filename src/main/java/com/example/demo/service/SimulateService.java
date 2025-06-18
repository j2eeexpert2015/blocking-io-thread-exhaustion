package com.example.demo.service;

import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Service;

@Service
public class SimulateService {

    // Simulate blocking I/O on platform thread
    @Timed(value = "simulate_blockingio_execution", description = "Time taken to execute blocking I/O")
    public void simulateBlockingIO() {
        try {
            Thread.sleep(2000); // Simulate I/O latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Simulate I/O on virtual thread
    @Timed(value = "simulate_virtual_execution", description = "Time taken to execute virtual I/O simulation")
    public void simulateWithVirtualThread() {
        try {
			Thread.startVirtualThread(() -> {
			    try {
			        Thread.sleep(2000); // Simulate I/O-like wait
			    } catch (InterruptedException e) {
			        Thread.currentThread().interrupt();
			    }
			}).join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
