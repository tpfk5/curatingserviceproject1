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

    //CSS -> card.badge 혼잡도 색 표시
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
        // 추가 및 수정 해야 할 듯?

        //혼잡도 상태
        //여유, 보통, 약간붐빔, 붐빔, 미정(OR 정보없음)
        switch (congestionNm) {
            case "여유"
                : isGreen = true;
            case "보통"
                : isOrange = true;
            case "약간붐빔"
                : isYellow = true;
            case "붐빔"
                : isRed = true;
            default: //미정
                isGray = true;

        }
    }

}
