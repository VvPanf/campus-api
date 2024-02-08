package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room, Long> {
}
