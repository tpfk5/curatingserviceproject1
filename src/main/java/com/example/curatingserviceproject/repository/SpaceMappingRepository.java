package com.example.curatingserviceproject.repository;

import com.example.curatingserviceproject.entity.SpaceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceMappingRepository extends JpaRepository<SpaceMapping, Long> {
    Optional<SpaceMapping> findByDisplaySiteKey(String displaySiteKey);
    Optional<SpaceMapping> findTopByAgncNmAndSpaceNm(String nm, String spaceNm);
}
