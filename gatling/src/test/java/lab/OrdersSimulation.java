package lab;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

/** Scenario: list orders, then create one. Assertions decide pass/fail. */
public class OrdersSimulation extends Simulation {

    HttpProtocolBuilder protocol = http
            .baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
            .acceptHeader("application/json");

    ScenarioBuilder orders = scenario("Orders")
            .exec(http("list orders").get("/orders").check(status().is(200)))
            .pause(1)
            .exec(http("create order")
                    .post("/orders")
                    .header("Content-Type", "application/json")
                    .body(StringBody("{\"sku\":\"BOOK-1\",\"quantity\":2}"))
                    .check(status().is(201)));

    {
        setUp(orders.injectOpen(constantUsersPerSec(10).during(Duration.ofSeconds(30))))
                .protocols(protocol)
                .assertions(
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile(95).lt(300));
    }
}
