# test-performance-chaos

Six small labs that test an Orders service under load, benchmark a hot method, and inject faults, so you can compare Gatling, k6, JMeter, JMH, Toxiproxy and Chaos Monkey for Spring Boot side by side.

## What is inside

| Folder | What it shows | Run |
| --- | --- | --- |
| [`gatling`](./gatling) | List-then-create Orders scenario as a Java simulation with failure-rate and p95 assertions | `mvn gatling:test` |
| [`k6`](./k6) | The same scenario as a k6 script with thresholds, plus the stub Orders API used by the load labs | `python3 stub_server.py &` then `docker run ... grafana/k6:0.54.0 run orders.js` |
| [`jmeter`](./jmeter) | The same scenario as a JMeter test plan with response-code and duration assertions | `docker run ... justb4/jmeter:5.5 -n -t orders.jmx` |
| [`jmh`](./jmh) | JMH benchmark showing dead-code elimination and constant folding next to correct versions | `mvn -q compile exec:exec` |
| [`toxiproxy`](./toxiproxy) | Latency and connection-cut injection through Toxiproxy, checked by a script | `docker compose up -d && python3 chaos.py` |
| [`chaos-monkey`](./chaos-monkey) | Chaos Monkey latency assault on a Spring Boot Orders service, verified by a test | `mvn -q test` |

## Prerequisites

- Java 21 and Maven 3.8 or newer (gatling, jmh, chaos-monkey)
- Python 3 (stub server and the Toxiproxy script)
- Docker (k6, jmeter, toxiproxy)

## How to read it

Start with `gatling` and `k6`: they run one Orders scenario with pass/fail thresholds, and `jmeter` repeats it as XML. Then read `jmh` for micro-benchmarks, and `toxiproxy` and `chaos-monkey` for two different ways to inject faults.
