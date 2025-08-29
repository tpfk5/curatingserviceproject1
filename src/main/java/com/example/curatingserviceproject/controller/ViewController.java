package com.example.curatingserviceproject.controller;


import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.service.DisplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {

    private final DisplayService displayService;

    @GetMapping("/cards")
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
}