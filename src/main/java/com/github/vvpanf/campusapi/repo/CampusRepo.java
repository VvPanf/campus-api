package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusRepo extends JpaRepository<Campus, Long> {
    Optional<Campus> findByName(String name);
}
