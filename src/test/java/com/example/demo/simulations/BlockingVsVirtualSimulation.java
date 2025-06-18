package com.example.demo.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class BlockingVsVirtualSimulation extends Simulation {

    // Configuration
    private static final String BASE_URL = "http://localhost:8080";
    private static final int USERS_PER_SCENARIO = 50; // Increased to stress test
    private static final int RAMP_UP_DURATION = 10; // seconds
    private static final int TEST_DURATION = 60; // seconds

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Load Test");

    // Scenario 1: Blocking I/O with Platform Threads
    // This should show thread pool exhaustion under load
    ScenarioBuilder blockingScenario = scenario("Blocking Platform Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Blocking Request")
                            .get("/api/simulate-blocking")
                            .check(status().is(200))
                    )
                    .pause(1, 2) // Short pause to create concurrent load
            );

    // Scenario 2: Virtual Thread Implementation  
    // This should handle the same load much better
    ScenarioBuilder virtualScenario = scenario("Virtual Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Virtual Thread Request")
                            .get("/api/simulate-virtual")
                            .check(status().is(200))
                    )
                    .pause(1, 2) // Same pause pattern for fair comparison
            );

    // Load Test Setup
    {
        setUp(
                // Run both scenarios concurrently to compare performance
                blockingScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                ),
                virtualScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                )
        )
        .protocols(httpProtocol)
        .assertions(
                // Performance assertions
                global().responseTime().max().lt(15000), // Max response time < 15 seconds
                global().successfulRequests().percent().gt(90.0), // 90% success rate
                forAll().responseTime().percentile3().lt(8000) // 99th percentile < 8 seconds
        );
    }
}