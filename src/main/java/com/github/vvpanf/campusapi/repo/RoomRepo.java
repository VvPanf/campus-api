package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findAllByCampusId(Long campusId);
    Page<Room> findAllByCampusId(Long campusId, Pageable pageable);
    Optional<Room> findByIdAndCampusId(Long id, Long campusId);
}
