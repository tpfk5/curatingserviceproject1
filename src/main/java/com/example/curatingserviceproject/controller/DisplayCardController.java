package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.service.DisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class DisplayCardController {

    private final DisplayService displayService;

    //DisplayCardDTO 정보 제공 -> cards.mustache 전달

    @GetMapping
    public String showCardView(Model model) {
        List<DisplayCardDTO> cards = displayService.getDisplayCards();
        model.addAttribute("cards",cards);
        return "cards"; //cards.mustache 와 연결
    }
}
