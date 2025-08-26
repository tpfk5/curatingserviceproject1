package com.example.curatingserviceproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "space_mapping", indexes = {
        @Index(name = "idx_display_site_key", columnList = "display_site_key", unique = true),
        @Index(name="idx_agnc_space",columnList = "agnc_nm, space_nm")
})

public class SpaceMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //'국립현대미술관 서울 제1전시실'
    @Column(name = "display_site_key", nullable = false, unique = true)
    private String displaySiteKey;

    //from congestion entity
    @Column(name="agnc_nm")
    private String agncNm;

    @Column(name = "space_nm")
    private String spaceNm;

    //전시실 코드
    @Column(name = "space_code",  nullable = false)
    private String spaceCode;

    //혼잡도 상태
    @Column (name = "congestion_nm", nullable = false)
    private String congestionNm;

}
