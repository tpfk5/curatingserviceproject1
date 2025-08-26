package com.example.curatingserviceproject.controller;
import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.service.DisplayService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CardViewController {

    private final DisplayService displayService;

    //완성된 카드 리스트 보여주기
    @GetMapping("/cards")
    public String getCards(Model model) {
        List<Display> displays = displayService.fetchANDSAVEDisplay(); //전체 전시

        List<DisplayCardDTO> cards = new ArrayList<>();
        for (Display display : displays) {
            DisplayCardDTO dto = new DisplayCardDTO(
                    display.getTITLE(),
                    display.getCNTC_INSTT_NM(),
                    display.getSpaceCode(),
                    display.getCongestionNm(),
//                    scoreCalc(display) //추천 점수 계산시 사용 예정
                    100 // 임시 점수
            );
            cards.add(dto);
        }
        model.addAttribute("cards", cards);
        return "cards";
    }
}
