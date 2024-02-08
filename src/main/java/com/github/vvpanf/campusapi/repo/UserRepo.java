package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByFullName(String fullName);
}
