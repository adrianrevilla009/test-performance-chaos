package lab;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/** The watched service is slowed by the 500 ms latency assault; the test sets its own pass/fail threshold. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChaosTest {

    @Autowired
    TestRestTemplate http;

    @Test
    void latencyAssaultSlowsOrders() {
        long start = System.nanoTime();
        var body = http.getForObject("/orders", String.class);
        long ms = (System.nanoTime() - start) / 1_000_000;
        assertTrue(body.contains("BOOK-1"));
        assertTrue(ms >= 500, "expected injected latency, got " + ms + " ms");
    }
}
