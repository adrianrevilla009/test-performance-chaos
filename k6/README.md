# k6

A k6 script (`orders.js`) for the Orders scenario, plus `stub_server.py`, a tiny Orders API the load labs call.

## Goal

Express the same list-then-create scenario as in the Gatling folder as a k6 script, with thresholds that decide pass or fail: error rate under 1% and p95 under 300 ms.

## Run it

```bash
python3 stub_server.py &                 # stand-in Orders API on :8080
docker run --rm -i --network host -v "$PWD:/w" -w /w grafana/k6:0.54.0 run orders.js
```

Expected: k6 prints the checks `list is 200` and `create is 201` and a tick next to each threshold. It exits non-zero when a threshold fails. Set `BASE_URL` to hit another target.

Not run end to end: in the environment where this was written the k6 container started but could not reach the stub on `localhost:8080` (Docker did not share the host network), so no threshold results were produced. The stub server itself was exercised by the Gatling run.

## What it proves

- `options` in `orders.js` defines 10 virtual users for 30 s and two thresholds, so the run is a pass/fail check rather than a report to read.
- `stub_server.py` answers `GET /orders` with the last 20 orders and `POST /orders` with 201 and an assigned id, which the checks assert.
- `BASE_URL` lets the same script target a real service.

## Trade-offs

- Scripts are JavaScript but k6 is not Node, so npm modules are not available by default.
- One machine limits the load you can generate.
- The stub keeps orders in memory in one Python process, so its latency says nothing about a real service.

## When not to use it

- When you need protocols beyond HTTP, gRPC and WebSocket, or a recorder, use JMeter.
- When load tests should live in the same JVM project as the code, Gatling fits better.
