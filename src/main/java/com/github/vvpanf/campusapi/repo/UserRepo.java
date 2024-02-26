package com.github.vvpanf.campusapi.repo;

import com.github.vvpanf.campusapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    Page<User> findByFullName(String fullName, Pageable pageable);
}
