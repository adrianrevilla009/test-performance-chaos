"""Tiny Orders API stand-in so the load scripts have something to hit. Not part of the lesson."""
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

ORDERS = []


class Handler(BaseHTTPRequestHandler):
    def _send(self, code, body):
        data = json.dumps(body).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def do_GET(self):
        self._send(200, ORDERS[-20:] if self.path == "/orders" else {"error": "not found"})

    def do_POST(self):
        body = json.loads(self.rfile.read(int(self.headers.get("Content-Length", 0))) or "{}")
        body["id"] = len(ORDERS) + 1
        ORDERS.append(body)
        self._send(201, body)

    def log_message(self, *args):
        pass


if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", 8080), Handler).serve_forever()
