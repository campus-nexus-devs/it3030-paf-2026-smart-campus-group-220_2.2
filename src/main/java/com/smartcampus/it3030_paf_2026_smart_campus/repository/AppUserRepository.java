package com.smartcampus.it3030_paf_2026_smart_campus.repository;

import com.smartcampus.it3030_paf_2026_smart_campus.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);
    Optional<AppUser> findByEmail(String email);
}
