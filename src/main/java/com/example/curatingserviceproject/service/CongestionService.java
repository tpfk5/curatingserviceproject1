package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.entity.Congestion;
import com.example.curatingserviceproject.repository.CongestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CongestionService {

    private final CongestionRepository congestionRepository;

    public Congestion fetchANDSAVECongestion(String spaceCode) {
        try {
            String apiUrl = "https://apis.data.go.kr/1371033/mmcadensity/congestion?" +
                    "serviceKey=TdJVbdw05eWxZJO%2Ff0ZX1IZOv2j1u%2BJ3JHze%2Bmp3mtBTg82ota042ELqefPV0oydKkLsP0ufsyFEqI7cqlXd%2Fw%3D%3D" +
                    "&spaceCode=" + spaceCode; // 전시실 코드

            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            StringBuilder result = new StringBuilder();
            String returnLine;
            while ((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine).append("\n");
            }

            JSONObject json = new JSONObject(result.toString());

            if (!json.optString("resultCode").equals("0000")) {
                throw new RuntimeException("API 응답 실패: " + json.optString("resultMsg"));
            }

            JSONObject data = json.getJSONObject("data");

            // 기존에 데이터가 있는지 먼저 확인 -> 최신 데이터 업데이트
            String spaceNm = data.optString("spaceNm", "");
            Optional<Congestion> existingCongestion =
                    congestionRepository.findTopBySpaceNmOrderByCollectedAtDesc(spaceNm);

            Congestion congestion;
            LocalDateTime now = LocalDateTime.now(); // 현재 시간 생성하기

            // 기존 데이터 있다면? -> 업데이트하기
            if (existingCongestion.isPresent()) {
                congestion = existingCongestion.get();
                congestion.setCongestionNm(data.optString("congestionNm", ""));
                congestion.setCollectedAt(now);

                log.info("새 혼잡도 데이터 생성: {}", spaceNm);
            }
            else {
                // 없다면? -> 최신 데이터 생성하기
                congestion = Congestion.builder()
                        .agncNm(data.optString("agncNm", ""))
                        .spaceNm(data.optString("spaceNm", ""))
                        .congestionNm(data.optString("congestionNm", ""))
                        .collectedAt(now)
                        .build();

                log.info("새 혼잡도 데이터 생성: {}", spaceNm);
            }

            return congestionRepository.save(congestion);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Congestion> getLatestCongestion(String spaceNm) {
        return congestionRepository.findTopBySpaceNmOrderByCollectedAtDesc(spaceNm);
    }
}
