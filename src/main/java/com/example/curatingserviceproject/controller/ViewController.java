package com.example.curatingserviceproject.controller;


import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.UserPreference;
import com.example.curatingserviceproject.service.DisplayService;
import com.example.curatingserviceproject.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {

    private final DisplayService displayService;
    private final RecommendationService recommendationService;

    @GetMapping("/cards-view")
    public String showCards(Model model) {
        try {
            List<DisplayCardDTO> cards = displayService.getDisplayCards();
            model.addAttribute("cards", cards);

            log.info("!카드 로딩 : {}", cards.size());
            return "cards";

        } catch (Exception e) {
            log.error("카드 로딩 실패", e);

            model.addAttribute("error", "데이터 로딩 실패");
            return "error";
        }
    }

    //인트로 페이지
    @GetMapping("/intro")
    public String intro() {
        return "intro";
    }


    //메인 페이지
    @GetMapping("/main")
    public String showMain(Model model, HttpServletRequest request) {

        try {
            String sessionId = request.getSession().getId();
            log.info("현재 세션 ID: {}", sessionId);

            UserPreference userPreference = recommendationService.getUserPreferenceBySession(sessionId);
            List<DisplayCardDTO> allCards = displayService.getDisplayCards(userPreference);

            if (userPreference != null && userPreference.getUserName() != null) {
                model.addAttribute("userName", userPreference.getUserName());
            }

            int limit = 3;
            List<DisplayCardDTO> limitedCards = allCards.size() > limit ?
                    allCards.subList(0,limit) : allCards;

            model.addAttribute("cards", limitedCards);
            model.addAttribute("allExhibitions", allCards);

            if(userPreference != null) {
                log.info("main: 사용자 취향 적용 시작 / 제한된 카드:{}, 전체 카드:{}", limitedCards, allCards);
            } else {
                log.info("main : 랜덤 정렬 시작 / 제한된 카드:{}, 전체 카드:{}", limitedCards, allCards);
            }
            return "main"; // -> main.mustache 이랑 연결하기

        } catch (Exception e) {
            log.error("로딩 실패", e);
            model.addAttribute("error", "로딩 실패");

            return "error";
        }
    }


    //detail page
    @GetMapping("/detail")
    public String detailByTitle(@RequestParam(required = false) String title, Model model, HttpServletRequest request) {
        try {
            List<DisplayCardDTO> allExhibitions = displayService.getDisplayCards();
            model.addAttribute("allExhibitions", allExhibitions);

            Display display;

            if (title != null && !title.isEmpty()) {
                display = displayService.getDisplayByTitle(title);
            } else {
                if (!allExhibitions.isEmpty()) {
                    display = displayService.getDisplayByTitle(allExhibitions.get(0).getTitle());
                } else {
                    log.error("전시 데이터가 없습니다.");
                    return "error";
                }
            }
            if (display == null) {
                log.error("해당 전시를 찾을 수 없습니다: {}", title);
                return "error";
            }

            // 기본 전시 정보
            model.addAttribute("TITLE", display.getTITLE());
            model.addAttribute("DESCRIPTION", display.getDESCRIPTION());
            model.addAttribute("AUTHOR", display.getAUTHOR());
            model.addAttribute("AGENCY", display.getCNTC_INSTT_NM());
            model.addAttribute("PERIOD", display.getPERIOD());
            model.addAttribute("congestionNm", display.getCongestionNm());
            model.addAttribute("imageObject", display.getIMAGE_OBJECT());

            //사용자 취향 조회
            String sessionId = request.getSession().getId();
            UserPreference userPreference = recommendationService.getUserPreferenceBySession(sessionId);
            log.info("Detail 페이지 사용자 취향: {}", userPreference != null ? userPreference.getUserName() : "없음");

            int congestionScore = recommendationService.calculateCongestionScore(display.getCongestionNm());
            int locationScore = recommendationService.calculateLocationPreferenceScore(display.getEVENT_SITE(), userPreference);
            int recommendScore = recommendationService.calculateRecommendationScore(display, userPreference);

            //scoreDetail점수
            DisplayCardDTO.ScoreDetail scoreDetail = new DisplayCardDTO.ScoreDetail();
            scoreDetail.setCongestionScore(congestionScore);
            scoreDetail.setLocationScore(locationScore);

            model.addAttribute("scoreDetail",scoreDetail);
            model.addAttribute("recommendScore",recommendScore);

            log.info("Detail 페이지 -> 전시: {}, 총점: {}", display.getTITLE(), recommendScore);
            return "detail";

        } catch (Exception e) {
            log.error("Detail 페이지 로딩 실패", e);
            model.addAttribute("error", "전시 정보 로딩 실패.");

            return "error";
        }
    }
}

