package com.funnydb.mutation.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class PrometheusMetricsHttpServer implements AutoCloseable {
    private final HttpServer server;

    public PrometheusMetricsHttpServer(String bindAddress, int port, MutationMetrics metrics) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(bindAddress, port), 0);
        this.server.createContext("/metrics", exchange -> writeText(exchange, 200, metrics.renderPrometheus()));
        this.server.createContext("/healthz", exchange -> writeText(exchange, 200, "ok\n"));
        this.server.setExecutor(Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "metrics-http-server");
            thread.setDaemon(true);
            return thread;
        }));
    }

    public void start() {
        server.start();
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private static void writeText(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}
