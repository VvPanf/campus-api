package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
}
