package com.example.curatingserviceproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "congestion")
public class Congestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agnc_nm")
    private String agncNm;        // 기관명(서울/과천/덕수궁/청주)

    @Column(name = "space_nm")
    private String spaceNm;       // 전시실명 (1전시실)

    @Column(name = "congestion_nm")
    private String congestionNm;  // 혼잡도 상태

    @Column(name = "collected_at")
    private LocalDateTime collectedAt; // 수집시각
}
