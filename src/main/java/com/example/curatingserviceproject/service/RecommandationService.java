package com.example.curatingserviceproject.service;

import jakarta.persistence.Entity;

/**
 * 사용자 추천 알고리즘 정리하기
 * 1. 혼잡도 (40)
 * 2. 장소 선호(서울/과천/덕수궁/청주) (20)
 * 3. 시간대 선호 (오전:10-12/오후:12-17/야간개장:17-21/노상관) (각 15)
 * 4. 관람비 선호 (유료/무료) (5)
 * 기본 점수(30)
 * 점수 구간 별 분류(ex. 80점 이상-> "지금 가기 좋은 전시" 등등)
 * 추천 이유도 적으면 좋을 것 같다?
 */


public class RecommandationService {
   // 시간대 선호
    public enum TimePreference {
        MORNING, AFTERNOON, EVENING, ANY
    }

    @Entity
    public class UserPreference {
        private TimePreference timePreference;
        private String selectedPlaces; // 서울 등등
    };

}
