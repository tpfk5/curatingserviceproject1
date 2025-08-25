package com.example.curatingserviceproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DisplayCardDTO {

    private String title; //전시 제목
//    private String agencyNm; // 전시실명
    private String spaceCode; //
    private String congestionNm; //혼잡도 상태
    private int recommendScore; //추천 점수

    public DisplayCardDTO(String title, String agency, String spaceCode, String congestionNm, int recommendScore) {
        this.title = title;
        this.spaceCode = spaceCode;
        this.congestionNm = congestionNm;
        this.recommendScore = recommendScore;
    }

}
