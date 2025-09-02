package com.example.curatingserviceproject.entity;

import com.example.curatingserviceproject.enums.TimePreference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "preferred_types", joinColumns = @JoinColumn(name = "user_preference_id"))
    @Column(name = "preferred_type")
    private List<String> preferredType; // from RecommendationService -> 선호 전시 유형

    @Column(name = "session_id", nullable = false)
    private String sessionId; //개인 식별자@@@
}