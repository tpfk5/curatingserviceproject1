package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.service.DisplayService;
import com.example.curatingserviceproject.entity.Display;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CuratingController {

    private final DisplayService displayService;

    @GetMapping("/api/fetch-displays")
    public String fetchDisplays() {
        try {
            List<Display> displays = displayService.fetchANDSAVEDisplay();
            return "호출 성공, 저장: " + displays.size();
        } catch (Exception e) {
            return "호출 오류 발생: " + e.getMessage();
        }
    }

    @GetMapping("/api/displays")
    public List<Display> displays() {
        return displayService.getAllDisplays();
    }

}