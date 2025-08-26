package com.example.curatingserviceproject.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DisplayCardDTO {

    private String title; //전시 제목
    private String agencyNm; // 전시실 명
    private String spaceCode; // 전시실 코드
    private String congestionNm; //혼잡도 상태
    private int recommendScore; //추천 점수

    //CSS -> card.badge
    private boolean isGreen;
    private boolean isOrange;
    private boolean isRed;
    private boolean isYellow;
    private boolean isGray;


    public DisplayCardDTO(String title, String agency, String spaceCode, String congestionNm, int recommendScore) {
        this.title = title;
        this.agencyNm = agency;
        this.spaceCode = spaceCode;
        this.congestionNm = congestionNm;
        this.recommendScore = recommendScore;

        //혼잡도 상태
        //여유, 보통, 약간붐빔, 붐빔, 미정
        switch (congestionNm) {
            case "여유":
                this.isGreen = true;
            case "보통":
                this.isOrange = true;
            case "약간붐빔":
                this.isYellow = true;
            case "붐빔":
                this.isRed = true;
            default: //미정 or 정보없음
                this.isGray = true;
        }
    }

}
