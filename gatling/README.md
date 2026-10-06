# gatling

A Gatling Java simulation (`src/test/java/lab/OrdersSimulation.java`) that lists orders, then creates one, with pass/fail assertions.

## Goal

Run one Orders scenario as code and let the build fail when the service is too slow or too error-prone. The `k6` and `jmeter` folders run the same scenario.

## Run it

```bash
python3 ../k6/stub_server.py &     # stand-in Orders API on :8080
mvn gatling:test
```

Expected: Gatling prints progress for the `list orders` and `create order` requests, then
`failed 0 (0%)`, both assertion lines ending in `true`, and `BUILD SUCCESS`. The HTML report is written under `target/gatling/`. Pinned: Gatling 3.13.1, gatling-maven-plugin 4.12.0, Java 21.

## What it proves

- The load shape is in code: an open model of 10 new users per second for 30 seconds, each doing `GET /orders`, a 1 s pause, then `POST /orders`.
- Success criteria sit next to the scenario: under 1% failed requests and p95 response time under 300 ms. If either fails, Maven fails the build.
- Against the stub server a run finished with 0 failed requests and a p95 of 12 ms.

## Trade-offs

- The typed Java DSL and the HTML report are good, but the toolchain is heavier than k6.
- An open model measures what an arrival rate does to the service; a closed model would hide queueing.
- The stub is a single Python process, so the numbers only show that the wiring works.
- The compiler plugin is pinned to 3.13.0 so that `maven.compiler.release` is honoured.

## When not to use it

- For a quick one-off check, a k6 script is lighter.
- When non-developers need a GUI to build plans, JMeter fits better.
