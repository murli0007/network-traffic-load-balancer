package com.cn.backend_server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final SeatRepository repo;

    public DataLoader(SeatRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {                       // seed only once
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= 500; i++) seats.add(new Seat("A" + i));
            repo.saveAll(seats);
            System.out.println("Created 500 seats");
        }
    }
}
