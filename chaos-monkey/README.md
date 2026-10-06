# chaos-monkey

A Spring Boot 3.3.5 Orders service (`OrdersApplication.java`) with Chaos Monkey for Spring Boot 3.1.0 configured in `application.yml`, and `ChaosTest.java`.

## Goal

Inject a latency assault into the service layer of a small Spring Boot app and check the effect with a test that sets its own pass/fail threshold.

## Run it

```bash
mvn -q test
```

Expected: the Spring context starts with the `chaos-monkey` profile active (the Chaos Monkey banner appears in the log), `ChaosTest` runs and Maven reports no failures. The log is noisy with Spring bean post-processor warnings; they are harmless. Pinned: Spring Boot 3.3.5, chaos-monkey-spring-boot 3.1.0, Java 21.

## What it proves

- `application.yml` activates the `chaos-monkey` profile, watches `@Service` beans, and sets `level: 1` with a latency range of 500 to 500 ms, so every call to `OrderService` is delayed by exactly 500 ms.
- `ChaosTest.latencyAssaultSlowsOrders` calls `GET /orders` and asserts the body contains `BOOK-1` and that the call took at least 500 ms.
- The `chaosmonkey` actuator endpoint is exposed next to `health`, so assaults can be changed at runtime. `exceptionsActive` is off; turn it on to try exception assaults.

## Trade-offs

- Assaults run inside the JVM, so they test application logic, not the network or infrastructure.
- Random assaults make tests flaky; this lab uses a fixed range and attacks every request.
- The test only shows the assault is active. A resilience test would assert that a timeout or fallback keeps the response within a budget, and this service has none.
- The actuator endpoint must never be exposed to untrusted users.

## When not to use it

- For network faults such as latency or cuts between services, use Toxiproxy.
- Do not enable it in production without guardrails and a way to switch it off.
