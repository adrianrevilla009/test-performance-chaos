package lab;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }

    @Service
    static class OrderService {
        List<String> list() {
            return List.of("BOOK-1 x2", "PEN-7 x10");
        }
    }

    @RestController
    static class OrderController {
        private final OrderService service;

        OrderController(OrderService service) {
            this.service = service;
        }

        @GetMapping("/orders")
        List<String> orders() {
            return service.list();
        }
    }
}
