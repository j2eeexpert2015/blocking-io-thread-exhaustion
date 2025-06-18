package com.example.demo.controller;

import com.example.demo.service.SimulateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulateController {

    private final SimulateService simulateService;

    public SimulateController(SimulateService simulateService) {
        this.simulateService = simulateService;
    }

    @GetMapping("/simulate")
    public String simulateBlocking() {
        simulateService.simulateBlockingIO();
        return "Blocking task completed";
    }

    @GetMapping("/simulate-virtual")
    public String simulateVirtual() {
        simulateService.simulateWithVirtualThread();
        return "Virtual task completed";
    }
}
