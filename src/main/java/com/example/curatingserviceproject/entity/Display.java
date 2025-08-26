package com.example.curatingserviceproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exhibitions")
public class Display {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String TITLE; //제목

    @Column(name = "collected_date")
    private String COLLECTED_DATE; //수집일

//    @Column(name = "DESCRIPTION")
//    private String DESCRIPTION; //소개

//    @Column(name = "view_count")
//    private Integer VIEW_COUNT; //조회수

    @Column(name = "cntc_instt_nm")
    private String CNTC_INSTT_NM; //연계기간명

    @Column(name = "event_site")
    private String EVENT_SITE; //장소

    @Column(name = "genre")
    private String GENRE; //장르

//    @Column(name = "DURATION")
//    private String DURATION; //관람시간

    @Column(name = "author")
    private String AUTHOR; //작가

    @Column(name = "charge")
    private String CHARGE; //관람료 정보

    @Column(name = "period")
    private String PERIOD; //기간

    @Column(name = "event_period")
    private String EVENT_PERIOD; //시간

    @Column(name = "space_code")
    private String SpaceCode;

    @Column(name = "congestion_nm")
    private String congestionNm;


}
