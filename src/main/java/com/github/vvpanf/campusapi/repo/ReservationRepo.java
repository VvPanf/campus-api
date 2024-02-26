package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    Page<Reservation> findAllByUserId(Long userId, Pageable pageable);
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
}
