package lab;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/** Orders domain: computing an order total. Shows dead-code elimination and constant folding. */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
@State(Scope.Thread)
public class PitfallsBenchmark {

    private final int[] pricesCents = new int[1_000];
    private int quantity = 3; // non-final: the JIT cannot fold it

    @Setup
    public void setup() {
        for (int i = 0; i < pricesCents.length; i++) pricesCents[i] = 100 + i;
    }

    private long total() {
        long sum = 0;
        for (int p : pricesCents) sum += (long) p * quantity;
        return sum;
    }

    /** WRONG: result unused, so the JIT may delete the whole loop. */
    @Benchmark
    public void deadCode() {
        total();
    }

    /** RIGHT: returning the value makes JMH consume it. */
    @Benchmark
    public long returned() {
        return total();
    }

    /** RIGHT: explicit Blackhole when there are several results. */
    @Benchmark
    public void blackhole(Blackhole bh) {
        bh.consume(total());
    }

    /** WRONG: constant inputs let the JIT compute the answer at compile time. */
    @Benchmark
    public long constantFolded() {
        return 1_000L * 3;
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(PitfallsBenchmark.class.getSimpleName()).build()).run();
    }
}
