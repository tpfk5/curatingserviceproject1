package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.dto.UserPreferenceDTO;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.UserPreference;
import com.example.curatingserviceproject.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
* 사용자 추천 알고리즘 정리하기
* 1. 혼잡도 상태 (25)
* 2. 개인 장소 선호(서울/과천/덕수궁/청주) (15)
* 3. 태그 선호 (20)
* 4. 상설/기획 선택 선호 (상설/기획) (10)
* 기본 점수(30)
* 점수 구간 별 분류(ex. 80점 이상-> "지금 가기 좋은 전시" 등등 표시하기)
* 추천 이유도 적으면 좋을 것 같다?? 언젠가
*/


@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final UserPreferenceRepository userPreferenceRepository;

    //추천 점수 계산
    public int calculateRecommendationScore(Display display) {
        return calculateRecommendationScore(display, null);
    }

    // 개인 추천 점수 계산
    public int calculateRecommendationScore(Display display, UserPreference userPreference) {
        log.info("전시 추천>> {}", display.getTITLE());

        int baseScore = 30; // 기본 점수 30

        // 1. 혼잡도 25
        int congestionScore = calculateCongestionScore(display.getCongestionNm());
        baseScore += congestionScore;

        log.info("혼잡도 점수: {}", congestionScore);

        // 2. 장소 선호도 15
        int locationPreferenceScore = calculateLocationPreferenceScore(display.getEVENT_SITE(), userPreference);
        baseScore += locationPreferenceScore;

        log.info("장소 선호도 점수: {}", locationPreferenceScore);

        // 3. 태그 점수 20
        int tagScore =calculateTagScore(display, userPreference);
        baseScore += tagScore;

        log.info("태그 점수: {}", tagScore);

        // 4. 전시 유형 10
        int typepreferenceScore = calculateTypePreferenceScore(userPreference, display);
        baseScore += typepreferenceScore;

        log.info("전시 유형 점수: {}", typepreferenceScore);

        // 총 점수 0-100
        int finalScore = Math.max(0, Math.min(100, baseScore));

        log.info("총 점수: {} - {}", finalScore, display.getTITLE());

        return finalScore;
    }

    // 혼잡도 점수 계산 25
    public int calculateCongestionScore(String congestionNm) {
        if (congestionNm == null || congestionNm.trim().isEmpty() || "미정".equals(congestionNm)) {
            return 8; // 기본 점수
        }
        switch (congestionNm.trim()) {
            case "여유":
                return 25;
            case "보통":
                return 18;
            case "약간붐빔":
                return 10;
            case "붐빔":
                return 3;
            case "매우붐빔":
                return 0;
            default:
                return 8;
        }
    }

    // 장소 선호도 점수 계산 15
    public int calculateLocationPreferenceScore(String eventSite, UserPreference userPreference) {
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

    // 태그 점수 계산(선택) -> 20점까지만
    public int calculateTagScore(Display display, UserPreference userPreference) {
        if (userPreference == null || userPreference.getPreferredTag() == null) {
            return 15; // 기본 점수
        }

        String displayTags = display.getTags();
        if (displayTags == null || displayTags.isEmpty()) {
            return 8;
        }

        String[] displayTagArray = displayTags.split(",");
        String[] userTagArray = userPreference.getPreferredTag().split(",");

        long matchCount = Arrays.stream(displayTagArray)
                .mapToLong(displayTag ->
                        Arrays.stream(userTagArray)
                                .anyMatch(userTag -> userTag.trim()
                                        .equals(displayTag.trim())) ? 1 : 0)
                .sum();

        int score = Math.min(20, (int)(matchCount * 10));
        log.info("태그 매칭: {}개, 점수: {}", matchCount, score);

        return score;
    }


    // 상설전,기획전 선택 점수 계산 10
    public int calculateTypePreferenceScore(UserPreference userPreference, Display display) {
        if (userPreference == null || userPreference.getPreferredType() == null) {
            return 6;
        }

        String userPreferredType = userPreference.getPreferredType();
        String exhibitionType = classifyExhibitionType(display);


        if (userPreferredType.equals(exhibitionType)) {
            return 10; //같으면 10
        }
        return 3; //같지 않으면 3
    }

    //전시 분류
    private String classifyExhibitionType(Display display) {
        String title = display.getTITLE();
        String exhibitionType = (title != null && title.contains("상설")) ? "상설전" : "기획전";

        return exhibitionType;
    }


    // event_site 위치 선호도
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
        } else if (site.contains("과천")) {
            return "과천";
        } else if (site.contains("청주")) {
            return "청주";
        }
        return "기타";
    }

    public void saveUserPreference(UserPreference userPreference) {
        userPreferenceRepository.save(userPreference);

    }

    public UserPreference getUserPreferenceBySession(String sessionId) {
        List<UserPreference> preferences = userPreferenceRepository.findBySessionId(sessionId);

        if (preferences.isEmpty()) {
            return null;
        }


        //user가 여러 번 취향 분석 시-> 가장 최근 거 반영하기
        return preferences.stream()
                .max((p1, p2) -> Long.compare(p1.getId(), p2.getId()))
                .orElse(null);

    }
}

