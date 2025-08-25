package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.entity.Congestion;
import com.example.curatingserviceproject.service.CongestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CongestionController {

    private final CongestionService congestionService;

    @GetMapping(value = "/api/congestion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCongestion(@RequestParam String spaceCode) {
        Congestion c = congestionService.fetchANDSAVECongestion(spaceCode);
        if (c != null) return ResponseEntity.ok(c);
        return ResponseEntity.status(500).body("혼잡도 API 호출 실패");
    }

    // Test용!!!!
    @GetMapping("/api/congestion/test")
    public ResponseEntity<?> test() {
        return getCongestion("MMCA-SPACE-1001"); //제1전시실 기준
    }
}
