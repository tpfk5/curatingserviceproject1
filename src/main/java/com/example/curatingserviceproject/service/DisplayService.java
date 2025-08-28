package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.SpaceMapping;
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
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayService {

    private final DisplayRepository displayRepository;
    private final SpaceMappingService spaceMappingService;
    private final RecommendationService recommendationService;

    public List<Display> fetchANDSAVEDisplay() {
        try {
            String xmlData = callApi();
            JSONObject jsonObject = XML.toJSONObject(xmlData);
            List<Display> displays = parseDisplaysFromJson(jsonObject);
            return displayRepository.saveAll(displays);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String callApi() throws Exception {
        StringBuilder result = new StringBuilder();

        String apiUrl = "https://api.kcisa.kr/openapi/API_CCA_145/request?" +
                "serviceKey=15cc63a0-9d9c-4ad1-bd58-6733a7487202&" +
                "numOfRows=100&" +
                "pageNo=26";

        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        String returnLine;
        while ((returnLine = bufferedReader.readLine()) != null) {
            result.append(returnLine).append("\n");
        }
        urlConnection.disconnect();
        return result.toString();

    }

    private List<Display> parseDisplaysFromJson(JSONObject jsonObject) {
        List<Display> displays = new ArrayList<>();

        try {
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray arr = items.getJSONArray("item");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);

                String agency = item.optString("CNTC_INSTT_NM", "");
                //'국립현대미술관' 필터링
                if (agency == null || !agency.equals("국립현대미술관")) {
                    continue;
                }

                String title = item.optString("TITLE", "");
                String eventSite = item.optString("EVENT_SITE", "");
                String displaySiteKey = eventSite;

                Display display = new Display();
                display.setTITLE(item.optString("TITLE", ""));
                display.setCOLLECTED_DATE(item.optString("COLLECTED_DATE", ""));
//              display.setDESCRIPTION(item.optString("DESCRIPTION", ""));
                display.setIMAGE_OBJECT(item.optString("IMAGE_OBJECT", ""));
                display.setEVENT_SITE(item.optString("EVENT_SITE", ""));
                display.setCHARGE(item.optString("CHARGE", ""));
                display.setPERIOD(item.optString("PERIOD", ""));
                display.setEVENT_PERIOD(item.optString("EVENT_PERIOD", ""));
                display.setCNTC_INSTT_NM(agency);

                try {
                    Optional<SpaceMapping> opt = spaceMappingService.getByDisplaySiteKey(displaySiteKey);
                    if (opt.isPresent()) {
                        SpaceMapping mapping = opt.get();
                        display.setSpaceCode(mapping.getSpaceCode());
                        display.setCongestionNm(mapping.getCongestionNm());

                        System.out.println("!혼잡도 매핑 확인!: " + mapping.getCongestionNm());
                    } else {
                        display.setSpaceCode("정보 없음");
                        display.setCongestionNm("정보 없음");
                    }
                } catch (Exception e) {
                    log.error("전시 파싱 실패", e);
                    display.setSpaceCode("오류");
                    display.setCongestionNm("오류");
                }
                displays.add(display); //리스트에 추가
            }
        } catch (Exception e) {
            log.error("파싱 실패", e);
        }
        return displays;
    }

    public List<Display> getAllDisplays() {
        return displayRepository.findAll();
    }

    //DisplayCard DTO
    public List<DisplayCardDTO> getDisplayCards() {
        List<Display> displays = displayRepository.findAll();
        Set<String> seenTitles = new HashSet<>(); //중복 방지용

        if (displays.isEmpty()) {
            log.warn("getDisplayCards 없음, API 호출");
            displays = fetchANDSAVEDisplay();
        }
        //'국립현대미술관'만 필터링
        List<DisplayCardDTO> result = new ArrayList<>();
        for (Display display : displays) {
            if (!"국립현대미술관".equals(display.getCNTC_INSTT_NM())) {
                continue;
            }

            //중복 체크하기 -> 이미 저장됐으면 SKIP!
            String title = display.getTITLE();
            if (seenTitles.contains(title)) {
                log.info("중복 skip: {}", title);
                continue;
            }
            seenTitles.add(title);

            //디버깅용
            log.info("처리중인 전시: {}", display.getTITLE());
            log.info("EVENT_SITE: {}", display.getEVENT_SITE());
            log.info("혼잡도: {}", display.getCongestionNm());
            log.info("관람료: {}", display.getCHARGE());


            int score;
            try {
                score = recommendationService.calculateRecommendationScore(display);

                String safeCongestionNm = display.getCongestionNm() != null ?
                        display.getCongestionNm() : "정보 없음";

                String spaceCode = display.getSpaceCode();
                String congestionNm = display.getCongestionNm();

                System.out.println("!혼잡도 상태!: " + congestionNm);
                System.out.println("!추천 점수!: " + score);

                DisplayCardDTO dto = new DisplayCardDTO(
                        title,
                        display.getCNTC_INSTT_NM(),
                        display.getSpaceCode(),
                        display.getIMAGE_OBJECT(),
                        safeCongestionNm,
                        score

                );
                result.add(dto);
            } catch (Exception e) {
                log.error("전시 처리 중 오류: {}", title, e);
            }
        }
        //높은 순으로 정렬??????? (내림차순)
        result.sort((a, b) -> Integer.compare(b.getRecommendScore(), a.getRecommendScore()));
        return result;
    }


    // main page 카드 개수 제한하기
    public List<DisplayCardDTO> getDisplayCardsLimited(int limit) {
        List<DisplayCardDTO> allcards = getDisplayCards();

        if (allcards.size() > limit) {
            return allcards.subList(0, limit);
        }
        return allcards;
    }
}


