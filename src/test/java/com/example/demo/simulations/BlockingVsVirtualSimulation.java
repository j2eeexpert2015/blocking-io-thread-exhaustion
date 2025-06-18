package com.example.demo.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class BlockingVsVirtualSimulation extends Simulation {

    // Base URL
    private static final String BASE_URL = "http://localhost:8080";

    // User count and duration
    private static final int USER_COUNT = 20;
    private static final int RAMP_UP_SECONDS = 10;
    private static final int TEST_DURATION_SECONDS = 30;

    // HTTP Protocol
    HttpProtocolBuilder httpProtocol = http.baseUrl(BASE_URL)
            .acceptHeader("application/json");

    // Blocking scenario
    ScenarioBuilder blockingScenario = scenario("Blocking I/O Scenario")
            .during(TEST_DURATION_SECONDS).on(
                    exec(http("Blocking Request")
                            .get("/simulate"))
            );

    // Virtual thread scenario
    ScenarioBuilder virtualScenario = scenario("Virtual Thread Scenario")
            .during(TEST_DURATION_SECONDS).on(
                    exec(http("Virtual Request")
                            .get("/simulate-virtual"))
            );

    {
        setUp(
                blockingScenario.injectOpen(rampUsers(USER_COUNT).during(RAMP_UP_SECONDS)),
                virtualScenario.injectOpen(rampUsers(USER_COUNT).during(RAMP_UP_SECONDS))
        ).protocols(httpProtocol);
    }
}
