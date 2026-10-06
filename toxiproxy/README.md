# toxiproxy

A `compose.yaml` with Toxiproxy in front of a static upstream, and `chaos.py`, which injects faults through the Toxiproxy admin API.

## Goal

Put a fault-injecting proxy between a client and an HTTP service, add 800 ms of latency and then a connection cut, and assert that the client sees both.

## Run it

```bash
docker compose up -d && sleep 3 && python3 chaos.py; docker compose down
```

Expected output of the script (run for real):

```
baseline       25 ms
latency       809 ms (injected 800)
cut        failed as expected: RemoteDisconnected
PASS
```

Needs Docker and Python 3. Image `ghcr.io/shopify/toxiproxy:2.9.0`; ports 8474 (admin) and 8666 (proxy). Local only, no cost.

## What it proves

- `chaos.py` creates a proxy `orders` listening on 8666 and forwarding to `upstream:8000`, and measures a baseline request.
- A `latency` toxic of 800 ms makes the same request take at least 800 ms; the script asserts it.
- A `limit_data` toxic with 0 bytes cuts the connection and the client gets an error; the script asserts that too.

## Trade-offs

- Faults act on TCP traffic through one proxy, so the app must be pointed at the proxy port.
- The upstream is Python's static `http.server`, so this proves the injection works, not that an application is resilient.
- The script uses only `urllib`, so there is nothing to install.

## When not to use it

- For faults inside the JVM (exceptions, slow methods), use Chaos Monkey for Spring Boot (`chaos-monkey` folder).
- For failures of pods or nodes, use a platform-level chaos tool.
