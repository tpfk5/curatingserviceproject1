package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.dto.DisplayCardDTO;
import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.repository.DisplayRepository;
import com.example.curatingserviceproject.entity.Display;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.json.XML;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DisplayService {

    private final DisplayRepository displayRepository;
    private final SpaceMappingService spaceMappingService;

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
                "pageNo=1";

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
                String displaySiteKey = item.optString("TITLE", "");

                //'국립현대미술관' 필터링
                if (agency == null || !agency.equals("국립현대미술관")) {
                    continue;
                }

                Display display = new Display();
                display.setTITLE(item.optString("TITLE", ""));
                display.setCOLLECTED_DATE(item.optString("COLLECTED_DATE", ""));
//                display.setDESCRIPTION(item.optString("DESCRIPTION", ""));
//                display.setVIEW_COUNT(item.optInt("VIEW_COUNT"));
                display.setEVENT_SITE(item.optString("EVENT_SITE", ""));
                display.setGENRE(item.optString("GENRE", ""));
//                display.setDURATION(item.optString("DURATION", ""));
                display.setAUTHOR(item.optString("AUTHOR", ""));
                display.setCHARGE(item.optString("CHARGE", ""));
                display.setPERIOD(item.optString("PERIOD", ""));
                display.setEVENT_PERIOD(item.optString("EVENT_PERIOD", ""));
                display.setCNTC_INSTT_NM(agency);


                Optional<SpaceMapping> opt = spaceMappingService.getByDisplaySiteKey(displaySiteKey);
                if (opt.isPresent()) {
                    SpaceMapping mapping = opt.get();

                    display.setSpaceCode(mapping.getSpaceCode());
                    display.setCongestionNm(mapping.getCongestionNm());

                    System.out.println("!혼잡도 매핑 확인!: " + mapping.getCongestionNm());
                }

                else {
                    display.setSpaceCode("미정");
                    display.setCongestionNm("정보 없음");
                }

                displays.add(display);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return displays;
    }

    public List<Display> getAllDisplays() {
        return displayRepository.findAll();
    }

    //DisplayCard DTO
    public List<DisplayCardDTO> getDisplayCards() {
        List<Display> displays = displayRepository.findAll();
        List<DisplayCardDTO> result = new ArrayList<>();

        for (Display display : displays) {
            //'국립현대미술관'만 가져오기
            if (!"국립현대미술관".equals(display.getCNTC_INSTT_NM())){
                continue;
            }

            String spaceCode = display.getSpaceCode();
            String congestionNm = display.getCongestionNm();

            System.out.println("!혼잡도 상태!: " + congestionNm);

            int score = 100; //임시 점수

            DisplayCardDTO dto = new DisplayCardDTO(
                    display.getTITLE(),
                    display.getCNTC_INSTT_NM(),
                    spaceCode,
                    congestionNm,
                    score
            );

            result.add(dto);
        }
        return result;
    }
}
