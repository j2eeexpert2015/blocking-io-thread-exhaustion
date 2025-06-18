package com.example.demo.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class BlockingVsVirtualSimulation extends Simulation {

    // Configuration
    private static final String BASE_URL = "http://localhost:8080";
    private static final int USERS_PER_SCENARIO = 25;
    private static final int RAMP_UP_DURATION = 10; // seconds
    private static final int TEST_DURATION = 60; // seconds

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Load Test");

    // Scenario 1: Blocking I/O with Platform Threads
    ScenarioBuilder blockingScenario = scenario("Blocking Platform Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Blocking Request")
                            .get("/api/simulate-blocking")
                            .check(status().is(200))
                    )
                    .pause(1, 3) // Random pause between requests
            );

    // Scenario 2: Virtual Thread Implementation
    ScenarioBuilder virtualScenario = scenario("Virtual Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Virtual Thread Request")
                            .get("/api/simulate-virtual")
                            .check(status().is(200))
                    )
                    .pause(1, 3) // Random pause between requests
            );

    // Scenario 3: Async Platform Thread (Limited Pool)
    ScenarioBuilder asyncPlatformScenario = scenario("Async Platform Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Async Platform Request")
                            .get("/api/simulate-async-platform")
                            .check(status().is(200))
                    )
                    .pause(1, 3)
            );

    // Scenario 4: Async Virtual Thread
    ScenarioBuilder asyncVirtualScenario = scenario("Async Virtual Thread Scenario")
            .during(TEST_DURATION).on(
                    exec(http("Async Virtual Request")
                            .get("/api/simulate-async-virtual")
                            .check(status().is(200))
                    )
                    .pause(1, 3)
            );

    // Scenario 5: CPU Intensive Task
    ScenarioBuilder cpuIntensiveScenario = scenario("CPU Intensive Scenario")
            .during(TEST_DURATION).on(
                    exec(http("CPU Intensive Request")
                            .get("/api/simulate-cpu-intensive")
                            .check(status().is(200))
                    )
                    .pause(2, 5) // Longer pause for CPU-intensive tasks
            );

    // Load Test Setup
    {
        setUp(
                // Run all scenarios concurrently to simulate real-world load
                blockingScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                ),
                virtualScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                ),
                asyncPlatformScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                ),
                asyncVirtualScenario.injectOpen(
                        rampUsers(USERS_PER_SCENARIO).during(RAMP_UP_DURATION)
                ),
                cpuIntensiveScenario.injectOpen(
                        rampUsers(10).during(RAMP_UP_DURATION) // Fewer users for CPU-intensive tasks
                )
        )
        .protocols(httpProtocol)
        .assertions(
                // Performance assertions
                global().responseTime().max().lt(10000), // Max response time < 10 seconds
                global().successfulRequests().percent().gt(95.0), // 95% success rate
                forAll().responseTime().percentile3().lt(5000) // 99th percentile < 5 seconds
        );
    }
}