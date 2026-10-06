# jmeter

A JMeter test plan (`orders.jmx`) for the Orders list-then-create scenario.

## Goal

Show the same scenario as the Gatling and k6 folders as a versionable JMeter plan that runs headless: 10 threads over a 5 s ramp-up for 30 s, with a 1 s think time.

## Run it

```bash
python3 ../k6/stub_server.py &     # stand-in Orders API on :8080
docker run --rm --network host -v "$PWD:/t" -w /t justb4/jmeter:5.5 \
  -n -t orders.jmx -l results.jtl -e -o report
```

Expected: non-GUI mode (`-n`) prints a summary and writes `results.jtl` and an HTML dashboard in `report/`. Failed assertions count as errors in the summary.

Not run end to end: JMeter is not installed locally and the plan, written by hand as XML, was never executed or opened in the GUI. Open it once in JMeter to confirm it loads before relying on it.

## What it proves

- `GET /orders` carries a response-code assertion (200) and a duration assertion (300 ms).
- `POST /orders` sends a raw JSON body with a `Content-Type: application/json` header manager and asserts 201.
- A load plan is a plain XML file that can run in CI without the GUI.

## Trade-offs

- The XML diffs badly in review, even though the GUI makes plans easy to build.
- Assertions are per sample; there is no pass/fail threshold on a percentile like p95.
- Thread-per-user costs more than Gatling or k6 at high concurrency.

## When not to use it

- When plans should be reviewed like code, use k6 or Gatling.
- Prefer it only when you need its protocol range (JDBC, JMS, LDAP) or a recorder.
