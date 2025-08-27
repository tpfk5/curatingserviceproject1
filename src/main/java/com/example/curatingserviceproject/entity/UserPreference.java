package com.example.curatingserviceproject.entity;

import com.example.curatingserviceproject.enums.TimePreference;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "time_preference")
    private TimePreference timePreference;

    @Column(name = "preferred_locations")
    private String preferredLocations; //from RecommendationService -> 선호 장소

    @Column(name = "user_session")
    private String userSession; //개인 식별자
}