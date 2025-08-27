package com.example.curatingserviceproject.repository;

import com.example.curatingserviceproject.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserSession(String userSession);
}
