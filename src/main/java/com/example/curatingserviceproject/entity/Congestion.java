package com.example.curatingserviceproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "congestion")
public class Congestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AGNC_NM")
    private String agncNm;        // 기관명(서울/과천/덕수궁/청주)

    @Column(name = "SPACE_NM")
    private String spaceNm;       // 전시실명 (ex. 1전시실)

    @Column(name = "CONGESTION_NM")
    private String congestionNm;  // 혼잡도 상태

    @Column(name = "COLLECTED_AT")
    private LocalDateTime collectedAt; // 수집시각
}
