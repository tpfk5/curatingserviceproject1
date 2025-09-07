package com.example.curatingserviceproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preferred_locations")
    private String preferredLocations; //from RecommendationService -> 선호 장소

    @Column(name = "preferred_type")
    private String preferredType; // from RecommendationService -> 선호 전시 유형

    @Column(name = "preferred_tag")
    private String preferredTag; //from RecommendationService -> 선호 전시 태그

    @Column(name = "session_id", nullable = false)
    private String sessionId; //개인 식별자

    @Column(name = "user_name")
    private String userName; //사용자 이름
}