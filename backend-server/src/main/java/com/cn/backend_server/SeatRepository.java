package com.cn.backend_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SeatRepository extends JpaRepository<Seat, String> {

    // Atomic: only succeeds if the seat is still free.
    // Returns 1 if booked now, 0 if someone already took it.
    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.booked = true WHERE s.id = :id AND s.booked = false")
    int book(@Param("id") String id);
}
