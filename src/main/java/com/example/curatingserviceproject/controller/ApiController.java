package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.Congestion;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final DisplayService displayService;
    private final CongestionService congestionService;
    private final SpaceMappingService spaceMappingService;
    private final RecommendationService recommendationService;

    //전시 정보
    @GetMapping("/api/displays")
    public List<Display> displays() {

        return displayService.getAllDisplays();
    }


    //전시 정보 저장, 수집
    @GetMapping("/api/fetch-displays")
    public ResponseEntity<?> fetchDisplays() {
        try {
            List<Display> saved = displayService.fetchANDSAVEDisplay(1,30);
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

    //추천 전시 카드 데이터
    @GetMapping("/api/cards")
    public ResponseEntity<?> getDisplayCards() {
        try {
            List<DisplayCardDTO> cardList = displayService.getDisplayCards();
            return ResponseEntity.ok(cardList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/api/congestion")
    public ResponseEntity<?> getCongestion(@RequestParam String spaceCode) {
        try {
            Congestion congestion = congestionService.fetchANDSAVECongestion(spaceCode);
            if (congestion != null) {
                return ResponseEntity.ok(congestion);
            }
                return ResponseEntity.status(404).body(Map.of("error", "혼잡도 없음"));

        } catch(Exception e){

            log.error("혼잡도 조회 오류", e);

            return ResponseEntity.status(500).body(Map.of("error", "혼잡도 API 호출 실패"));
            }
        }


    //혼잡도 테스트
//    @GetMapping("/api/congestion/test")
//    public ResponseEntity<?> testCongestion() {
//        return getCongestion("MMCA-SPACE-1001"); //1전시실 테스트
//    }


    @GetMapping("/api/mappings")
    public ResponseEntity<?> getDisplaySiteKey(@RequestParam(required = false) String displaySiteKey) {
        try {
            log.info("매핑 조회하기, getDisplaySiteKey: {}", displaySiteKey);

            if (displaySiteKey == null || displaySiteKey.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("displaySiteKey 없음");
            }

            Optional<SpaceMapping> opt = spaceMappingService.getByDisplaySiteKey(displaySiteKey.trim());

            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "displaySiteKey 없음" + displaySiteKey));
            }
        } catch (Exception e) {
            log.error("매핑 조회 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error: ", e.getMessage()));
        }
    }

    @PostMapping("/api/mappings")
    public ResponseEntity<?> createUpdateMapping(@RequestBody Map<String, String> request) {
        try {
            log.info("매핑 생성 or 수정 요청: {}", request);

            String displaySiteKey = request.get("displaySiteKey");
            String spaceCode = request.get("spaceCode");

            if (displaySiteKey == null || displaySiteKey.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "displaySiteKey 없음"));
            }

            if (spaceCode == null || spaceCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "spaceCode 없음"));
            }

            SpaceMapping result = spaceMappingService.upsert(
                    displaySiteKey.trim(),
                    request.getOrDefault("agncNm", ""),
                    request.getOrDefault("spaceNm", ""),
                    spaceCode.trim(),
                    request.getOrDefault("congestionNm", "")
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("매핑 생성/수정 오류 발생!", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //사용자 취향 분석 저장
//    @PostMapping("/api/savePreference")
//    public ResponseEntity<?> saveUserPreference(@RequestBody UserPreference userPreference) {
//        recommendationService.saveUserPreference(userPreference);
//        return ResponseEntity.ok("사용자 취향 저장 완료!");
//    }



    //% 테스트 용!!!!%
    @GetMapping("/api/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API 컨트롤러 작동중!");
    }

    @GetMapping("/api/mappings/test")
    public ResponseEntity<String> testMapping() {
        return ResponseEntity.ok("매핑 컨트롤러 작동중!");
            }


    }






