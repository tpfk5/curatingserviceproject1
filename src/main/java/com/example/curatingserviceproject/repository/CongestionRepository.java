package com.example.curatingserviceproject.repository;

import com.example.curatingserviceproject.entity.Congestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CongestionRepository extends JpaRepository<Congestion, Long> {
    Optional<Congestion> findTopBySpaceNmOrderByCollectedAtDesc(String spaceNm);
}
