import http from 'k6/http';
import { check, sleep } from 'k6';

// Scenario: browse the order list, then create an order.
export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],   // under 1% errors
    http_req_duration: ['p(95)<300'], // p95 under 300 ms
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const list = http.get(`${BASE}/orders`);
  check(list, { 'list is 200': (r) => r.status === 200 });

  const created = http.post(
    `${BASE}/orders`,
    JSON.stringify({ sku: 'BOOK-1', quantity: 2 }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  check(created, { 'create is 201': (r) => r.status === 201 });
  sleep(1);
}
