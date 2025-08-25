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

    @Column(name = "TITLE")
    private String TITLE; //제목

    @Column(name = "COLLECTED_DATE")
    private String COLLECTED_DATE; //수집일

//    @Column(name = "DESCRIPTION")
//    private String DESCRIPTION; //소개

    @Column(name = "VIEW_COUNT")
    private Integer VIEW_COUNT; //조회수

    @Column(name = "CNTC_INSTT_NM")
    private String CNTC_INSTT_NM; //연계기간명

    @Column(name = "EVENT_SITE")
    private String EVENT_SITE; //장소

    @Column(name = "GENRE")
    private String GENRE; //장르

    @Column(name = "DURATION")
    private String DURATION; //관람시간

    @Column(name = "AUTHOR")
    private String AUTHOR; //작가

    @Column(name = "CHARGE")
    private String CHARGE; //관람료 할인

    @Column(name = "PERIOD")
    private String PERIOD; //기간

    @Column(name = "EVENT_PERIOD")
    private String EVENT_PERIOD; //시간


}
