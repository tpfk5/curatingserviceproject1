package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.Congestion;
import com.example.curatingserviceproject.entity.Display;
import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.service.CongestionService;
import com.example.curatingserviceproject.service.DisplayService;
import com.example.curatingserviceproject.service.SpaceMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/displays")
    public List<Display> displays() {
        return displayService.getAllDisplays();
    }

    @GetMapping("/api/fetch-displays")
    public ResponseEntity<?> fetchDisplays() {
        try {
            List<Display> saved = displayService.fetchANDSAVEDisplay();
            return ResponseEntity.ok(Map.of(
                    "status","ok",
                    "savedCount",saved.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status","error",
                    "message",e.getMessage()
            ));
        }
    }
    @GetMapping("/api/cards")
    public ResponseEntity<?> getDisplayCards() {
        try {
            List<DisplayCardDTO> cardList = displayService.getDisplayCards();
            return ResponseEntity.ok(cardList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    public ResponseEntity<?> getCongestion(@RequestParam String spaceCode) {
        Congestion congestion = congestionService.fetchANDSAVECongestion(spaceCode);
        if (congestion != null)
            return ResponseEntity.ok(congestion);
        return ResponseEntity.status(500).body("혼잡도 API 호출 실패");
    }

    @GetMapping("/api/congestion/test")
    public ResponseEntity<?> testCongestion() {
        return getCongestion("MMCA-SPACE-1001");
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

    public ResponseEntity<?> getAllMappings() {
        try {
            log.info("전체 목록 조회 시작!");

            List<SpaceMapping> allMappings = spaceMappingService.getAllMappings();

            log.info("all 매핑 목록 조회 완료! 개수:{}", allMappings.size());

            return ResponseEntity.ok(allMappings);

        } catch (Exception e) {
            log.error("매핑 조회 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<?> upsert (@RequestBody Map<String, String> request) {
        try {
            log.info("매핑 생성 or 수정 요청: {}", request);

            String displaySiteKey = request.get("displaySiteKey");
            String agncNm = request.get("agncNm");
            String spaceNm = request.get("spaceNm");
            String spaceCode = request.get("spaceCode");
            String congestionNm = request.get("congestionNm");

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
                    agncNm != null ? agncNm.trim() : "",
                    spaceNm != null ? spaceNm.trim() : "",
                    spaceCode.trim(),
                    congestionNm != null ? congestionNm.trim() : ""
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("요청 잘못됨: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("매핑 생성 or 수정 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }

    // 테스트 용!!!!
    @GetMapping("/api/mappings/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("컨트롤러 작동중!");
    }

    @GetMapping("/api/mappings/test-service")
    public ResponseEntity<String> testService() {
        try {
            List<SpaceMapping> mappings = spaceMappingService.getAllMappings();
            return ResponseEntity.ok("Service 연결 성공! 매핑 수: " + mappings.size());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서비스 오류: " + e.getMessage());
        }
    }
}



