"""Inject latency, then a connection cut, through Toxiproxy's HTTP API and assert the client sees it."""
import json
import time
import urllib.request

API = "http://localhost:8474"
PROXIED = "http://localhost:8666/"


def api(method, path, body=None):
    req = urllib.request.Request(API + path, method=method, data=json.dumps(body).encode() if body else None)
    with urllib.request.urlopen(req) as r:
        return r.read()


def timed_get():
    start = time.monotonic()
    try:
        urllib.request.urlopen(PROXIED, timeout=5).read()
        return time.monotonic() - start, None
    except Exception as e:  # noqa: BLE001 - any failure is the signal here
        return time.monotonic() - start, e


def main():
    try:
        api("DELETE", "/proxies/orders")
    except Exception:  # noqa: BLE001 - proxy may not exist yet
        pass
    api("POST", "/proxies", {"name": "orders", "listen": "0.0.0.0:8666", "upstream": "upstream:8000"})

    base, err = timed_get()
    assert err is None, err
    print(f"baseline   {base * 1000:6.0f} ms")

    api("POST", "/proxies/orders/toxics",
        {"name": "slow", "type": "latency", "attributes": {"latency": 800}})
    slow, err = timed_get()
    assert err is None and slow >= 0.8, (slow, err)
    print(f"latency    {slow * 1000:6.0f} ms (injected 800)")
    api("DELETE", "/proxies/orders/toxics/slow")

    api("POST", "/proxies/orders/toxics",
        {"name": "cut", "type": "limit_data", "attributes": {"bytes": 0}})
    _, err = timed_get()
    assert err is not None, "expected the connection to be cut"
    print(f"cut        failed as expected: {type(err).__name__}")
    api("DELETE", "/proxies/orders")
    print("PASS")


if __name__ == "__main__":
    main()
