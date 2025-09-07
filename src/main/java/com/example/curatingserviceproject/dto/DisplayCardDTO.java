package com.example.curatingserviceproject.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DisplayCardDTO {

    private String title; //전시 제목
    private String imageObject; //이미지 url
    private String agencyNm; // 전시실 명
    private String spaceCode; // 전시실 코드
    private String congestionNm; //혼잡도 상태
    private int recommendScore; //추천 점수
    private String period; //전시 기간
    private String description; //전시 설명
    private String author; //작가
    private ScoreDetail scoreDetail; // 추천 상세 점수


    //main page -> card.badge 혼잡도 색 표시
    private boolean isGreen;
    private boolean isOrange;
    private boolean isRed;
    private boolean isYellow;
    private boolean isGray;



    public DisplayCardDTO(String title, String imageObject, String agencyNm, String spaceCode, String congestionNm, int recommendScore, String period, String description, String author) {
        this.title = title != null ? title : "제목 없음";
        this.imageObject = imageObject != null ? imageObject : "/img/temp.jpg";
        this.agencyNm = agencyNm != null ? agencyNm : "기관 정보 없음";
        this.spaceCode = spaceCode != null ? spaceCode : "공간 정보 없음";
        this.congestionNm = congestionNm != null ? congestionNm : "혼잡도 정보 없음";
        this.recommendScore = recommendScore;
        this.period = period;
        this.description = description;
        this.author = author;

        //혼잡도 상태
        //여유, 보통, 약간붐빔, 붐빔, 미정(OR 정보없음, 기본 표시)
        switch (congestionNm) {
            case "여유":
                isGreen = true;
                break;

            case "보통":
                isOrange = true;
                break;

            case "약간붐빔":
                isYellow = true;
                break;

            case "붐빔":
                isRed = true;
                break;

            default: //미정
                isGray = true;

        }

    }

    // 추천 점수
    @Getter @Setter
    public static class ScoreDetail {
        private int congestionScore;
        private int locationScore;
        private int tagScore;
        private int baseScore = 20;
    }

    public void setScoreDetail(int congestionScore, int locationScore, int tagScore) {
        ScoreDetail detail = new ScoreDetail();
        detail.setCongestionScore(congestionScore);
        detail.setLocationScore(locationScore);
        detail.setTagScore(tagScore);

        this.scoreDetail = detail;
    }
}

