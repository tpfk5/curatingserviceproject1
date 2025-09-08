package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.dto.UserPreferenceDTO;
import com.example.curatingserviceproject.entity.Congestion;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.entity.UserPreference;
import com.example.curatingserviceproject.repository.DisplayRepository;
import com.example.curatingserviceproject.service.*;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TagExtractionService tagExtractionService;
    private final DisplayRepository displayRepository;

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
    public ResponseEntity<?> getDisplayCardsWithPreference
    (@RequestParam(required = false) String sessionId,
     @RequestParam(required = false) String preferredLocations,
     @RequestParam(required = false) String preferredTag
    ){
        UserPreference userPreference = null;

        if (sessionId != null && !sessionId.trim().isEmpty()) {
            userPreference = recommendationService.getUserPreferenceBySession(sessionId);

        } else if (preferredLocations != null ) {
            userPreference = new UserPreference();
            userPreference.setPreferredLocations(preferredLocations);
        }

        List<DisplayCardDTO> cardList = displayService.getDisplayCards(userPreference);
        return ResponseEntity.ok(cardList);
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

    @GetMapping("/api/mappings/all")
    public ResponseEntity<?> getAllMappings() {
        try {
            List<SpaceMapping> mappings = spaceMappingService.getAllMappings();
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("전체 매핑 조회 오류:", e);
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    @PostMapping("/api/admin/update-mappings")
    public ResponseEntity<?> updateExistingDisplayMappings() {
        try {
            List<Display> displays = displayRepository.findAll();
            int updatedCount = 0;

            for (Display display : displays) {
                String displaySiteKey = display.getEVENT_SITE();
                try {
                    Optional<SpaceMapping> opt = spaceMappingService.getByDisplaySiteKey(displaySiteKey);
                    if (opt.isPresent()) {
                        SpaceMapping mapping = opt.get();
                        display.setSpaceCode(mapping.getSpaceCode());
                        display.setCongestionNm(mapping.getCongestionNm());
                        updatedCount++;
                        log.info("매핑 업데이트: {} -> {}", display.getTITLE(), mapping.getSpaceCode());
                    }
                } catch (Exception e) {
                    log.error("매핑 업데이트 실패: {}", display.getTITLE(), e);
                }
            }

            displayRepository.saveAll(displays);

            return ResponseEntity.ok(Map.of(
                    "status", "completed",
                    "updatedCount", updatedCount
            ));
        } catch (Exception e) {
            log.error("전체 매핑 업데이트 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/api/admin/update-mappings-get")
    public ResponseEntity<?> updateExistingDisplayMappingsGet() {
        return updateExistingDisplayMappings();
    }





    //사용자 취향 분석 저장
    @PostMapping("/api/preference")
    public ResponseEntity<?> submitPreference(@RequestBody UserPreferenceDTO dto,
                                              HttpServletRequest request) {
        try {
            String sessionId = request.getSession().getId();

            UserPreference userPreference = UserPreference.builder()
                    .sessionId(sessionId)
                    .userName(dto.getUserName())
                    .preferredLocations(dto.getPreferredLocation())
                    .preferredTag(dto.getPreferredTag())
                    .build();


            recommendationService.saveUserPreference(userPreference);

            return ResponseEntity.ok(Map.of("status", "saved"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    //전시 추천 태그 생성하기
    @PostMapping("/api/admin/generate-tags")
    public ResponseEntity<?> generateTags() {
        List<Display> displays = displayService.getAllDisplays();
        int tagCount = 0;

        for (Display display: displays) {
            String tags = tagExtractionService.extractTags(display);
            display.setTags(tags);
            tagCount++;

            log.info("전시: '{}' -> 태그: '{}'", display.getTITLE(), tags);
        }
        displayRepository.saveAll(displays);

        return ResponseEntity.ok(Map.of(
                "status","completed",
                "tagCount",tagCount,
                "message","태그 생성 완료!"

        ));
    }
    //테스트용@
    @GetMapping("/api/admin/generate-tags-get")
    public ResponseEntity<?> generateTagsGet() {
        return generateTags();
    }


    }






