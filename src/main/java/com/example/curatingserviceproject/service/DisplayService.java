package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.entity.UserPreference;
import com.example.curatingserviceproject.repository.DisplayRepository;
import com.example.curatingserviceproject.entity.Display;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayService {

    private final DisplayRepository displayRepository;
    private final SpaceMappingService spaceMappingService;
    private final RecommendationService recommendationService;
    private final TagExtractionService tagExtractionService;

    public List<Display> fetchANDSAVEDisplay(int startPage, int endPage) {
        List<Display> allDisplays = new ArrayList<>();

        //여러 페이지 불러오기);
            try {
            for (int page = startPage; page <= endPage; page++) {
                String xmlData = callApi(page);
                JSONObject jsonObject = XML.toJSONObject(xmlData);

                List<Display> displays = parseDisplaysFromJson(jsonObject);
                allDisplays.addAll(displays);
            }
            return displayRepository.saveAll(allDisplays);

        } catch (Exception e) {
            log.error("API 호출/저장 중 오류", e);
            return new ArrayList<>();
        }
    }

    //특정 페이지만 불러오기!!
    private String callApi(int pageNo) throws Exception {
        StringBuilder result = new StringBuilder();

        String apiUrl = "https://api.kcisa.kr/openapi/API_CCA_145/request?" +
                "serviceKey=15cc63a0-9d9c-4ad1-bd58-6733a7487202&" +
                "numOfRows=100&" +
                "pageNo=" + pageNo;

        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);

        urlConnection.connect();

        try (
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(),
                        "UTF-8"))) {
            String returnLine;

            while ((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine).append("\n");
            }
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }

    private List<Display> parseDisplaysFromJson(JSONObject jsonObject) {
        List<Display> displays = new ArrayList<>();
        Set<String> seenLocalIds = new HashSet<>();

        try {
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray arr = items.getJSONArray("item");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);

                //'국립현대미술관'만 필터링 하기
                String agency = item.optString("CNTC_INSTT_NM", "");
                if (!"국립현대미술관".equals(agency)) {
                    continue;
                }

                //'현재' 전시 중인 전시만 필터링 하기
                String period = item.optString("PERIOD", "");
                if (!isCurrentlyExhibited(period)) {
                    continue;
                }

                //'전시 id' 로 중복 전시 필터링 하기
                String localId = item.optString("LOCAL_ID", "");
                if (localId.isEmpty() || seenLocalIds.contains(localId)) {
                    continue;
                }
                seenLocalIds.add(localId);

                Display display = new Display();
                display.setTITLE(item.optString("TITLE", ""));
                display.setCOLLECTED_DATE(item.optString("COLLECTED_DATE", ""));
                display.setEVENT_SITE(item.optString("EVENT_SITE", ""));
                display.setCHARGE(item.optString("CHARGE", ""));
                display.setPERIOD(item.optString("PERIOD", ""));
                display.setCNTC_INSTT_NM(item.optString("CNTC_INSTT_NM",""));

                display.setDESCRIPTION(item.optString("DESCRIPTION", ""));
                display.setAUTHOR(item.optString("AUTHOR", ""));


                //img url 가져오기
                String imageUrl = item.optString("IMAGE_OBJECT", "");
                if (imageUrl.isEmpty()) {
                    imageUrl = "/img/temp.jpg"; // && 임시 기본 이미지&&
                }
                display.setIMAGE_OBJECT(imageUrl);

                log.info("!이미지 URL: {}", display.getIMAGE_OBJECT());

                String displaySiteKey = display.getEVENT_SITE();
                try {
                    Optional<SpaceMapping> opt = spaceMappingService.getByDisplaySiteKey(displaySiteKey);
                    if (opt.isPresent()) {
                        SpaceMapping mapping = opt.get();
                        display.setSpaceCode(mapping.getSpaceCode());
                        display.setCongestionNm(mapping.getCongestionNm());
                        log.info("!혼잡도 매핑 확인!: {}",mapping.getCongestionNm() );

                    } else {
                        display.setSpaceCode("정보 없음");
                        display.setCongestionNm("정보 없음");
                    }
                } catch (Exception e) {
                    log.error("SpaceMapping 조회 실패", e);
                    display.setSpaceCode("오류");
                    display.setCongestionNm("오류");
                }

                //전시 태그 추출하기
                String tags = tagExtractionService.extractTags(display);
                display.setTags(tags);
                log.info("생성된 태그: {} -> {}", display.getTITLE(), tags);

                displays.add(display); //리스트에 추가
            }
        } catch (Exception e) {
            log.error("파싱 실패", e);
        }
        return displays;
    }


    //현재 전시중인 전시만 필터링 학기
    private boolean isCurrentlyExhibited(String period) {
        if (period == null || !period.contains("~")) {
            return false;
        }

    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] dates = period.split("~");
        LocalDate startDate = LocalDate.parse(dates[0].trim(),formatter);
        LocalDate endDate = LocalDate.parse(dates[1].trim(),formatter);
        LocalDate now = LocalDate.now();

       boolean result = (now.isEqual(startDate) || now.isAfter(startDate)) &&
                (now.isEqual(endDate) || now.isBefore(endDate));

        return result;


    } catch (Exception e) {
        log.error("기간 파싱 실패: {}", period, e);
        return false;
    }
    }

    public List<Display> getAllDisplays() {
        return displayRepository.findAll();
    }

    //임시로 중복 전시 2개 안보이게 하기
    private static final Set<String> EXCLUDED_TITLES = Set.of(
            "젊은 모색 2025", "MMCA 서울 상설전: 한국현대미술 하이라이트"
    );


    //DisplayCard DTO <- 사용자 취향 반영@
    public List<DisplayCardDTO> getDisplayCards(UserPreference preference) {
        Set<String> seenTitles = new HashSet<>(); //중복 방지용
        List<Display> displays = displayRepository.findAll();
        List<DisplayCardDTO> result = new ArrayList<>();

        if (displays.isEmpty()) {
            log.warn("DB 없음. 새 데이터 수집 시작!");

            displays = fetchANDSAVEDisplay(1, 30);
            log.info("새로 수집된 전시 수: {}", displays.size());
        }

        //'국립현대미술관'만 필터링
        for (Display display : displays) {
            if (!"국립현대미술관".equals(display.getCNTC_INSTT_NM())) {
                continue;
            }

            if (!isCurrentlyExhibited(display.getPERIOD())) {
                continue;
            }

            //중복 체크하기 -> 이미 저장됐으면 SKIP!
            String title = display.getTITLE();

            if (EXCLUDED_TITLES.contains(title)) {
                log.info("제외된 전시:, {}", title);
                continue;
            }

            if (seenTitles.contains(title)) {
                log.info("중복 skip: {}", title);
                continue;
            }

            seenTitles.add(title);
            try {
                int score = recommendationService.calculateRecommendationScore(display, preference);
                String safeCongestionNm = display.getCongestionNm() != null ? display.getCongestionNm() : "정보 없음";

                DisplayCardDTO dto = new DisplayCardDTO(
                        title,
                        display.getIMAGE_OBJECT(),
                        display.getCNTC_INSTT_NM(),
                        display.getSpaceCode(),
                        safeCongestionNm,
                        score,
                        display.getPERIOD(),
                        display.getDESCRIPTION(),
                        display.getAUTHOR()
                );
                log.info("@이미지 URL: {}", display.getIMAGE_OBJECT());

                //점수 상세 정보 추가하기
                if (preference != null) {
                    int congestionScore = recommendationService.calculateCongestionScore(display.getCongestionNm());
                    int locationScore = recommendationService.calculateLocationPreferenceScore(display.getEVENT_SITE(), preference);
                    int tagScore = recommendationService.calculateTagScore(display, preference);

                    dto.setScoreDetail(congestionScore, locationScore, tagScore);
                }

                result.add(dto);
            } catch (Exception e) {
                log.error("!추천 점수 계산 중 오류: {}", title, e);
            }
        }

        if (preference != null) {
            // 사용자 취향 0 -> 버튼 -> 점수 높은 순 정렬
            result.sort((a, b) -> Integer.compare(b.getRecommendScore(), a.getRecommendScore()));
            log.info("점수 순 정렬");
        }
        // x -> 랜덤 정렬
        else {
            Collections.shuffle(result);
            log.info("랜덤 정렬");
        }

        return result;
    }


    // main page에서 카드 개수 제한하기(우선 3개만 랜덤으로)
    public List<DisplayCardDTO> getDisplayCardsLimited(int limit) {
        List<DisplayCardDTO> allcards = getDisplayCards();

        if (allcards.size() > limit) {
            return allcards.subList(0, limit);
        }
        return allcards;
    }

    //main.view btn -> detail 페이지로 이동
    public Display getDisplayByTitle(String title) {
        List<Display> displays = displayRepository.findAll();

        return displays.stream()
                .filter(display -> display.getTITLE().equals(title))
                .findFirst()
                .orElse(null);
    }

//    메서드 오버라이딩>?
    public List<DisplayCardDTO> getDisplayCards() {
        return getDisplayCards(null);
    }


}


