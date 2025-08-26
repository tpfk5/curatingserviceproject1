package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.service.DisplayService;
import com.example.curatingserviceproject.entity.Display;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DisplayController {

    private final DisplayService displayService;

    @GetMapping("/api/fetch-displays")
    public ResponseEntity<?> fetchDisplays() {
        try {
            List<Display> saved = displayService.fetchANDSAVEDisplay();
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "savedCount", saved.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/api/displays")
    public List<Display> displays() {
        return displayService.getAllDisplays();
    }

    //전체 전시 정보 리스트 -> 카드 형태로 생성
    @GetMapping("/api/cards")
    public ResponseEntity<?> getDisplayCards() {
        try{
            List< DisplayCardDTO> cardList = displayService.getDisplayCards();
            return ResponseEntity.ok(cardList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}