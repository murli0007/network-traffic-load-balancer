package com.cn.backend_server;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Seat {
    @Id
    private String id;          // e.g. "A12"
    private boolean booked;

    public Seat() {}
    public Seat(String id) { this.id = id; this.booked = false; }

    public String getId() { return id; }
    public boolean isBooked() { return booked; }
}
