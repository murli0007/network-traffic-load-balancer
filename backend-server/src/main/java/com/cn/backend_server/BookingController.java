package com.cn.backend_server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class BookingController {
    private final SeatRepository repo;

    @Value("${server.port}")
    private int port;

    public BookingController(SeatRepository repo) { this.repo = repo; }

    @PostMapping("/book")
    public Map<String, Object> book(@RequestParam String seat) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));   // simulate processing time
        boolean ok = repo.book(seat) == 1;
        return Map.of("success", ok, "seat", seat, "server", port);
    }

    @GetMapping("/api/seats")
    public List<Seat> seats() {
        List<Seat> list = new ArrayList<>(repo.findAll());
        list.sort(Comparator.comparingInt((Seat s) -> Integer.parseInt(s.getId().substring(1))));
        return list;
    }
}
