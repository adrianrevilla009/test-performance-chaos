# jmh

A JMH benchmark (`src/main/java/lab/PitfallsBenchmark.java`) that computes an order total four ways, two correct and two broken.

## Goal

Show two classic micro-benchmark pitfalls, dead-code elimination and constant folding, next to the correct way to write the same benchmark. Warm-up and measurement are set explicitly: 3 warm-up and 3 measured iterations of 1 s, one fork.

## Run it

```bash
mvn -q compile exec:exec
```

Expected: JMH prints a table of average time per operation. A real run gave:

```
PitfallsBenchmark.blackhole       avgt  3  502.998 ± 43.820  ns/op
PitfallsBenchmark.constantFolded  avgt  3    0.785 ±  0.094  ns/op
PitfallsBenchmark.deadCode        avgt  3    1.316 ±  0.272  ns/op
PitfallsBenchmark.returned        avgt  3  506.195 ± 12.748  ns/op
```

Pinned: JMH 1.37, Java 21.

## What it proves

- `deadCode` discards the result of `total()`, so the JIT can remove the loop: about 1 ns instead of about 500 ns.
- `constantFolded` returns `1_000L * 3`, which the compiler computes ahead of time: under 1 ns.
- `returned` (return the value) and `blackhole` (pass it to a `Blackhole`) both measure the real 1,000-element loop at about 500 ns.

## Trade-offs

- Short iterations keep the lab quick but the error bars wide; use more iterations and forks for real decisions.
- JMH times a hot, isolated method, not a request path with I/O, GC pressure or contention.
- Absolute numbers depend on the machine and the JVM.

## When not to use it

- For end-to-end latency or throughput of a service, use a load tool (`gatling`, `k6`, `jmeter`).
- For code that is not a hot, CPU-bound path, where a microbenchmark misleads.
