package com.example.curatingserviceproject.controller;

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
public class CuratingController {

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

}