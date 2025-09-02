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
    @GetMapping("/main")
    public String showmain(Model model) {
        try {
            int limit = 3;
            List<DisplayCardDTO> cards = displayService.getDisplayCardsLimited(limit);
            List<DisplayCardDTO> allExhibitions = displayService.getDisplayCards();

            log.info("vc에서 받은 카드 수:{}", cards.size());
            log.info("전체 전시 수: {}", allExhibitions.size());

            model.addAttribute("cards", cards);
            model.addAttribute("allExhibitions", allExhibitions);

            return "main"; // -> main.mustache 이랑 연결하기

        } catch (Exception e) {
            log.error("로딩 실패", e);
            model.addAttribute("error", "로딩 실패");
            return "error";
        }
    }


    //detail page
    @GetMapping("/detail")
    public String detailByTitle(@RequestParam(required = false) String title, Model model) {
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

            model.addAttribute("TITLE", display.getTITLE());
            model.addAttribute("DESCRIPTION", display.getDESCRIPTION());
            model.addAttribute("AUTHOR", display.getAUTHOR());
            model.addAttribute("AGENCY", display.getCNTC_INSTT_NM());
            model.addAttribute("PERIOD", display.getPERIOD());
            model.addAttribute("congestionNm", display.getCongestionNm());
            model.addAttribute("imageObject", display.getIMAGE_OBJECT());

            // @@임시 분석 점수@@@
            model.addAttribute("congestionScore", "8.5");
            model.addAttribute("locationScore", "7.2");
            model.addAttribute("timeScore", "6.8");
            model.addAttribute("popularityScore", "9.1");
            model.addAttribute("recommendScore", "50");

            log.info("Detail page - 선택 전시: {}, 전체 수: {}",
                    display.getTITLE(), allExhibitions.size());


            return "detail";
        } catch (Exception e) {

            log.error("Detail 페이지 로딩 실패", e);

            model.addAttribute("error", "전시 정보 로딩 실패.");
            return "error";
        }
    }

//    @GetMapping("/detail-ui")
//    public String detailByTitle(@RequestParam(required = false) String title, Model model) {
//
//        Display display;
//
//        if (title == null || title.isEmpty()) {
//            // title 파라미터 없으면 더미 데이터 생성
//            display = new Display();
//            display.setTITLE("Patty Chang’s Arbitrary Acts of Devotion");
//            display.setDESCRIPTION("Alternating between particular and general experience—the death of one whale, ...");
//            display.setAUTHOR("Erin Schwartz");
//            display.setCNTC_INSTT_NM("NY Review of Books");
//            display.setPERIOD("January 14, 2018");
//            display.setCongestionNm("Moderate");
//            display.setIMAGE_OBJECT("/images/sample-image.jpg"); // 프로젝트 내 임시 이미지 경로
//        } else {
//            display = displayService.getDisplayByTitle(title);
//        }
//
//        if (display == null) {
//            return "error";
//        }
//
//        model.addAttribute("TITLE", display.getTITLE());
//        model.addAttribute("DESCRIPTION", display.getDESCRIPTION());
//        model.addAttribute("AUTHOR", display.getAUTHOR());
//        model.addAttribute("AGENCY", display.getCNTC_INSTT_NM());
//        model.addAttribute("PERIOD", display.getPERIOD());
//        model.addAttribute("congestionNm", display.getCongestionNm());
//        model.addAttribute("imageObject", display.getIMAGE_OBJECT());
//
//        return "detail";
//    }


    //my ui 수정 테스트용@@@@
    @GetMapping("/my-ui")
    public String myUI(Model model) {
        model.addAttribute("userName", "김철수");
        model.addAttribute("favoriteCount", 3);

        // 찜한 전시 목록 (임시)
        List<String> favorites = Arrays.asList(
                "김환기: 어디서 무엇이 되어 다시 만나랴",
                "현대미술의 새로운 시각",
                "추상과 구상 사이"
        );

        model.addAttribute("favoriteExhibitions", favorites);

        // 추천받은 전시들 (임시)
        List<String> recommended = Arrays.asList(
                "한국화의 현재", "조각의 미학", "사진예술전"
        );
        model.addAttribute("recommendedExhibitions", recommended);
        model.addAttribute("preferredType", "현대미술");

        return "my";
    }
}