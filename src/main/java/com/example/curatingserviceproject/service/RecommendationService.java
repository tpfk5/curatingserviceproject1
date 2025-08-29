package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.UserPreference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.example.curatingserviceproject.enums.TimePreference.*;

/**
* 사용자 추천 알고리즘 정리하기
* 1. 혼잡도 상태 (35)
* 2. 개인 장소 선호(서울/과천/덕수궁/청주) (15)
* 3. 시간대 선호 (오전:10-12/오후:12-17/야간개장:17-21/노상관) (각 15,,,)
* 4. 관람비 선호 (유료/무료/노상관) (5)
* 5. 인기 점수 (10)
* 기본 점수(20)
* 점수 구간 별 분류(ex. 80점 이상-> "지금 가기 좋은 전시" 등등 표시하기)
* 추천 이유도 적으면 좋을 것 같다?? 언젠가
*/


@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    //추천 점수 계산
    public int calculateRecommendationScore(Display display) {
        return calculateRecommendationScore(display, null);
    }

    // 개인화 추천 점수 계산
    public int calculateRecommendationScore(Display display, UserPreference userPreference) {
        log.info("전시 추천>> {}", display.getTITLE());

        int baseScore = 20; // 기본 점수 20

        // 1. 혼잡도 35
        int congestionScore = calculateCongestionScore(display.getCongestionNm());
        baseScore += congestionScore;

        log.debug("혼잡도 점수: {}", congestionScore);

        // 2. 인기 20
        int locationPreferenceScore = calculateLocationPreferenceScore(display.getEVENT_SITE(), userPreference);
        baseScore += locationPreferenceScore;

        log.debug("인기 점수: {}", locationPreferenceScore);

        // 3. 시간대 15
        int timePreferenceScore = calculateTimePreferenceScore(userPreference);
        baseScore += timePreferenceScore;

        log.debug("시간대 점수: {}", timePreferenceScore);

        // 4. 관람비 5
        int chargeScore = calculateChargeScore(display.getCHARGE());
        baseScore += chargeScore;

        log.debug("관람비 점수: {}", chargeScore);

        // 5. 인기 10
        int popularityScore = calculatePopularityScore(display.getEVENT_SITE());
        baseScore += popularityScore;

        log.debug("인기 점수: {}", popularityScore);

        // 총 점수 0-100
        int finalScore = Math.max(0, Math.min(100, baseScore));

        log.info("총 점수: {} - {}", finalScore, display.getTITLE());

        return finalScore;
    }

    // 혼잡도 점수 계산 35
    private int calculateCongestionScore(String congestionNm) {
        if (congestionNm == null || congestionNm.trim().isEmpty() || "미정".equals(congestionNm)) {
            return 8; // 기본 점수
        }

        switch (congestionNm.trim()) {
            case "여유":
                return 35;
            case "보통":
                return 25;
            case "약간붐빔":
                return 15;
            case "붐빔":
                return 5;
            case "매우붐빔":
                return 0;
            default:
                return 8;
        }
    }

    // 장소 선호도 점수 계산 15
    private int calculateLocationPreferenceScore(String eventSite, UserPreference userPreference) {
        if (userPreference == null || userPreference.getPreferredLocations() == null) {
            return 5;
        }

        String location = extractLocation(eventSite);
        List<String> preferredLocations = Arrays.asList(
                userPreference.getPreferredLocations().split(",")
        );

        if (preferredLocations.contains("상관 없음") || preferredLocations.contains(location)) {
            return 15;
        }
        return 2;
    }

    // 시간대 선호 점수 계산 15
    private int calculateTimePreferenceScore(UserPreference userPreference) {
        if (userPreference == null || userPreference.getTimePreference() == null) {
            return 8;
        }

        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        switch (userPreference.getTimePreference()) {
            case MORNING:
                return (10 <= hour && hour < 12) ? 15 : 3;
            case AFTERNOON:
                return (12 <= hour && hour < 18) ? 15 : 3;
            case EVENING: //서울/과천, 수/토요일 한정
                return (18 <= hour && hour < 21) ? 15 : 3;
            case ANY:
                return (10 <= hour && hour < 21) ? 15 : 8;
            default:
                return 8;
        }
    }

    // 관람비 점수 계산 5
    private int calculateChargeScore(String charge) {
        if (charge == null || charge.trim().isEmpty()) {
            return 1;
        }

        if (charge.contains("무료") || charge.equals("0")) {
            return 5;
        } else { //!수정해야함!
            return 3;
        }
    }

    //  @@@유료/무료/노상관 선택지에 따른 사용자 선호 점수제로 할건지 결정해야함@@ 0829 14:29

    // 인기도 계산 15
    private int calculatePopularityScore(String eventSite) {
        String location = extractLocation(eventSite);

        switch (location) {
            case "서울":
                return 10;
            case "과천":
                return 8;
            case "청주":
                return 5;
            case "덕수궁":
                return 3;
            default:
                return 1;
        }
    }

    // event_site 위치
    private String extractLocation(String eventSite) {
        if (eventSite == null) {
            log.warn("!eventSite is null!");
            return "기타";
        }

        String site = eventSite.toLowerCase();

        if (site.contains("서울")) {
            return "서울";
        } else if (site.contains("덕수궁")) {
            return "덕수궁";
        } else if (site.contains("과천") ) {
            return "과천";
        } else if (site.contains("청주")) {
            return "청주";
        }
        return "기타";
    }
    }

