package com.example.curatingserviceproject.controller;


import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.service.DisplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {

    private final DisplayService displayService;

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


    //html / css 화면 보여주기
//    @GetMapping("/main")
//    public String showmain(Model model) {
//        try {
//            int limit = 3;
//            List<DisplayCardDTO> cards = displayService.getDisplayCardsLimited(limit);
//            List<DisplayCardDTO> allExhibitions = displayService.getDisplayCards();
//
//            log.info("vc에서 받은 카드 수:{}", cards.size());
//            log.info("전체 전시 수: {}", allExhibitions.size());
//
//            model.addAttribute("cards", cards);
//            model.addAttribute("allExhibitions", allExhibitions);
//
//            return "main"; // -> main.mustache 이랑 연결하기
//
//        } catch (Exception e) {
//            log.error("로딩 실패", e);
//            model.addAttribute("error", "로딩 실패");
//            return "error";
//        }
//    }

    @GetMapping("/main-ui")
    public String showMain(Model model) {
        try {

            List<DisplayCardDTO> dummyCards = Arrays.asList(
                    new DisplayCardDTO(
                            "전시 A",
                            "/images/sample1.jpg",
                            "기관 A",
                            "SC001",
                            "여유",
                            85,
                            "2025-09-01 ~ 2025-10-01",
                            "전시 A 설명입니다.",
                            "작가 A"
                    ),
                    new DisplayCardDTO(
                            "전시 B",
                            "/images/sample2.jpg",
                            "기관 B",
                            "SC002",
                            "보통",
                            70,
                            "2025-09-05 ~ 2025-09-30",
                            "전시 B 설명입니다.",
                            "작가 B"
                    ),
                    new DisplayCardDTO(
                            "전시 C",
                            "/images/sample3.jpg",
                            "기관 C",
                            "SC003",
                            "붐빔",
                            60,
                            "2025-09-10 ~ 2025-10-15",
                            "전시 C 설명입니다.",
                            "작가 C"
                    )
            );

            // 더미 데이터에 임의로 점수 세부정보 설정 가능
            dummyCards.get(0).setScoreDetail(8, 7, 6, 5, 9);
            dummyCards.get(1).setScoreDetail(6, 5, 7, 8, 7);
            dummyCards.get(2).setScoreDetail(4, 6, 5, 7, 6);

            // 전체 전시 리스트 (예시로 동일 사용)
            List<DisplayCardDTO> allExhibitions = dummyCards;

            model.addAttribute("cards", dummyCards);
            model.addAttribute("allExhibitions", allExhibitions);

            return "main"; // main.mustache 또는 main.html 템플릿과 연결

        } catch (Exception e) {
            model.addAttribute("error", "로딩 실패");
            return "error";
        }
    }


    //detail page
//    @GetMapping("/detail")
//    public String detailByTitle(@RequestParam(required = false) String title, Model model) {
//        try {
//            List<DisplayCardDTO> allExhibitions = displayService.getDisplayCards();
//            model.addAttribute("allExhibitions", allExhibitions);
//
//            Display display;
//
//
//            if (title != null && !title.isEmpty()) {
//                display = displayService.getDisplayByTitle(title);
//            } else {
//                if (!allExhibitions.isEmpty()) {
//                    display = displayService.getDisplayByTitle(allExhibitions.get(0).getTitle());
//                } else {
//                    log.error("전시 데이터가 없습니다.");
//                    return "error";
//                }
//            }
//
//            if (display == null) {
//                log.error("해당 전시를 찾을 수 없습니다: {}", title);
//                return "error";
//            }
//
//            model.addAttribute("TITLE", display.getTITLE());
//            model.addAttribute("DESCRIPTION", display.getDESCRIPTION());
//            model.addAttribute("AUTHOR", display.getAUTHOR());
//            model.addAttribute("AGENCY", display.getCNTC_INSTT_NM());
//            model.addAttribute("PERIOD", display.getPERIOD());
//            model.addAttribute("congestionNm", display.getCongestionNm());
//            model.addAttribute("imageObject", display.getIMAGE_OBJECT());
//
//            // @@임시 분석 점수@@@
//            model.addAttribute("congestionScore", "8");
//            model.addAttribute("locationScore", "7");
//            model.addAttribute("timeScore", "6");
//            model.addAttribute("popularityScore", "9");
//            model.addAttribute("recommendScore", "50");
//
//            log.info("Detail page - 선택 전시: {}, 전체 수: {}",
//                    display.getTITLE(), allExhibitions.size());
//
//
//            return "detail";
//        } catch (Exception e) {
//
//            log.error("Detail 페이지 로딩 실패", e);
//
//            model.addAttribute("error", "전시 정보 로딩 실패.");
//            return "error";
//        }
//    }

    @GetMapping("/detail-ui")
    public String detailByTitle(@RequestParam(required = false) String title, Model model) {

        Display display;

        if (title == null || title.isEmpty()) {
            // title 파라미터 없으면 더미 데이터 생성
            display = new Display();
            display.setTITLE("Patty Chang’s Arbitrary Acts of Devotion");
            display.setDESCRIPTION("Alternating between particular and general experience—the death of one whale, ...");
            display.setAUTHOR("Erin Schwartz");
            display.setCNTC_INSTT_NM("NY Review of Books");
            display.setPERIOD("January 14, 2018");
            display.setCongestionNm("Moderate");
            display.setIMAGE_OBJECT("/images/sample-image.jpg"); // 프로젝트 내 임시 이미지 경로
        } else {
            display = displayService.getDisplayByTitle(title);
        }

        if (display == null) {
            return "error";
        }

        model.addAttribute("TITLE", display.getTITLE());
        model.addAttribute("DESCRIPTION", display.getDESCRIPTION());
        model.addAttribute("AUTHOR", display.getAUTHOR());
        model.addAttribute("AGENCY", display.getCNTC_INSTT_NM());
        model.addAttribute("PERIOD", display.getPERIOD());
        model.addAttribute("congestionNm", display.getCongestionNm());
        model.addAttribute("imageObject", display.getIMAGE_OBJECT());

        return "detail";
    }

    }
