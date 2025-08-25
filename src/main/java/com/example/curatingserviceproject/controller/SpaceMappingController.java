package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.service.SpaceMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mappings")
@Slf4j
public class SpaceMappingController {

    private final SpaceMappingService spaceMappingService;
    //-----------------------------------------------------
    //테스트용
    @GetMapping("/test")
    public ResponseEntity<String> test () {
        return ResponseEntity.ok("컨트롤러 작동중!");
    }

    @GetMapping("/test-service")
    public ResponseEntity<String> testService() {
        try {
            List<SpaceMapping> mappings = spaceMappingService.getAllMappings();
            return ResponseEntity.ok("Service 연결 성공! 현재 매핑 수: " + mappings.size());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서비스 오류: " + e.getMessage());
        }
    }
//-------------------------------------------------------------------------

    //GET(매핑 정보 조회용)
    @GetMapping
    public ResponseEntity<?> getDisplaySiteKey(@RequestParam(required = false) String displaySiteKey) {
        try {
            log.info("매핑 조회하기, getDisplaySiteKey: {}", displaySiteKey);

            //displaysitekey가 없거나 비었다면?? -> 파라미터 검증
            if (displaySiteKey == null || displaySiteKey.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("displaySiteKey 없음");
            }

//        if (trimmed.isEmpty()) {
//            return ResponseEntity
//                    .badRequest()
//                    .body("displaySiteKey 없음"); // 40
//        }

            Optional<SpaceMapping> opt =
                    spaceMappingService.getByDisplaySiteKey(displaySiteKey.trim());

            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "displaySiteKey 없음"
                                + displaySiteKey));
            }
        } catch (Exception e) {
            log.error("매핑 조회 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error: ", e.getMessage()));
        }
    }

    //GET(전체 목록 조회용)
    @GetMapping("/all")
    public ResponseEntity<?> getAllMappings() {
        try {
            log.info("전체 목록 조회 시작!");

            List<SpaceMapping> allMappings = spaceMappingService.getAllMappings();

            log.info("전체 매핑 목록 조회 완료! 개수: {}",  allMappings.size());

            return ResponseEntity.ok(allMappings);
        } catch (Exception e) {
            log.error("매핑 조회 오류 발생", e);
            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }

    }

//POST
//UPSERT (생성/ 수정)
    @PostMapping
    public ResponseEntity<?> upsert(@RequestBody Map<String, String> request) {
        try {
            log.info("매핑 생성/수정 요청: {}", request);

            String displaySiteKey = request.get("displaySiteKey");
            String agncNm = request.get("agncNm");
            String spaceNm = request.get("spaceNm");
            String spaceCode = request.get("spaceCode");

            if (displaySiteKey == null || displaySiteKey.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "displaySiteKey 없음"));
            }

            if (spaceCode == null || spaceCode.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "spaceCode 없음"));
            }
            SpaceMapping result = spaceMappingService.upsert(
                    displaySiteKey.trim(),
                    agncNm != null ? agncNm.trim() : "",
                    spaceNm != null ? spaceNm.trim() : "",
                    spaceCode.trim()
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("매핑 생성/수정 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }
}


//            // 기존 행 있는지 조회
//            Optional <SpaceMapping> opt =
//                    spaceMappingService.findByDisplaySiteKey(displaySiteKey);
//
//            SpaceMapping entity;
//            // 기존 행이 있다면? -? 수정하기
//            if  (opt.isPresent()) {
//                entity = opt.get();
//                entity.setAgncNm(agncNm);
//                entity.setSpaceCode(spaceCode);
//                entity.setDisplaySiteKey(displaySiteKey);
//
//                SpaceMapping saved = spaceMappingService.save(entity);
//
//                return ResponseEntity.ok(saved);
//            } else {
//                //기존 행이 없다면? -> 생성하기
//                entity = new SpaceMapping();
//                entity.setDisplaySiteKey(displaySiteKey);
//                entity.setAgncNm(agncNm);
//                entity.setSpaceCode(spaceCode);
//
//                SpaceMapping saved = spaceMappingService.save(entity);
//
//                return ResponseEntity.ok(saved);
//
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500)
//                    .body("오류 발생: " + e.getMessage());
//        }
//        }
//
//    }
//
