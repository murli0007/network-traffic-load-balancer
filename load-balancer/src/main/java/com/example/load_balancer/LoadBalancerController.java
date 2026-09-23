package com.example.load_balancer;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class LoadBalancerController {

    private final RestClient client = RestClient.create();

    private final List<String> servers = List.of(
            "http://localhost:8081",
            "http://localhost:8082",
            "http://localhost:8083"
    );

    private final int[] requestCount = new int[3];
    private final int[] activeRequests = new int[3];
    private final int[] successCount = new int[3];
    private final int[] failedCount = new int[3];

    @PostMapping("/book")
    public synchronized Map<String, Object> book(
            @RequestParam String seat) {

        int serverIndex = getLeastLoadedServer();

        activeRequests[serverIndex]++;
        requestCount[serverIndex]++;

        long start = System.currentTimeMillis();

        try {
            Map response = client.post()
                    .uri(servers.get(serverIndex) + "/book?seat=" + seat)
                    .retrieve()
                    .body(Map.class);

            long latency = System.currentTimeMillis() - start;

            boolean success = Boolean.TRUE.equals(response.get("success"));

            if (success) {
                successCount[serverIndex]++;
            } else {
                failedCount[serverIndex]++;
            }

            Map<String, Object> result = new HashMap<>();

            result.put("success", success);
            result.put("seat", seat);
            result.put("server", 8081 + serverIndex);
            result.put("latency", latency + " ms");

            return result;

        } catch (Exception e) {

            failedCount[serverIndex]++;

            return Map.of(
                    "success", false,
                    "error", "Server unavailable",
                    "server", 8081 + serverIndex
            );

        } finally {
            activeRequests[serverIndex]--;
        }
    }

    private int getLeastLoadedServer() {

        int index = 0;

        for (int i = 1; i < activeRequests.length; i++) {

            if (activeRequests[i] < activeRequests[index]) {
                index = i;
            }
        }

        return index;
    }

    @GetMapping("/admin/stats")
    public synchronized List<Map<String, Object>> stats() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            result.add(Map.of(
                    "server", "Server " + (i + 1),
                    "port", 8081 + i,
                    "requests", requestCount[i],
                    "active", activeRequests[i],
                    "success", successCount[i],
                    "failed", failedCount[i]
            ));
        }

        return result;
    }

    @GetMapping("/health")
    public String health() {
        return "Load Balancer is running";
    }

    @GetMapping("/api/seats")
    public Object seats() {

        return client.get()
                .uri("http://localhost:8081/api/seats")
                .retrieve()
                .body(Object.class);
    }
}
