package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.entity.Display;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagExtractionService {
    record TagRule (String keyword, String tag, double weight) {}

    private static final List<TagRule> TAG_RULES = List.of(
            // 설치/미디어
            new TagRule("설치", "설치/미디어", 1.2),
            new TagRule("installation", "설치/미디어", 1.2),
            new TagRule("미디어", "설치/미디어", 1.1),
            new TagRule("디지털", "설치/미디어", 1.0),
            new TagRule("영상", "설치/미디어", 1.0),
            new TagRule("video", "설치/미디어", 1.0),
            new TagRule("영화", "필름/사운드", 1.1),

            // 사진
            new TagRule("사진", "사진", 1.1),
            new TagRule("포토", "사진", 1.0),
            new TagRule("photography", "사진", 1.0),

            // 회화/수채
            new TagRule("회화", "회화/수채", 1.0),
            new TagRule("유화", "회화/수채", 1.0),
            new TagRule("드로잉", "회화/수채", 1.0),
            new TagRule("수채", "회화/수채", 1.2),

            //사운드
            new TagRule("사운드", "사운드", 1.1),
            new TagRule("음악", "사운드", 1.0),

            // 신인 작가
            new TagRule("신진 작가", "신인 작가", 1.1),
            new TagRule("올해의 작가상", "신인 작가", 1.1),

            // 자연/생태
            new TagRule("숲", "자연", 1.2),
            new TagRule("생태", "자연", 1.1),

            // 근현대
            new TagRule("현대미술", "근현대", 1.0),
            new TagRule("근현대미술", "근현대", 1.1)

    );

    public String extractTags(Display display) {
        String content = (display.getTITLE() + " " +
                (display.getDESCRIPTION() != null ? display.getDESCRIPTION() : ""))
                .toLowerCase();

        Map<String, Double> tagScore = new HashMap<>();

        for (TagRule rule : TAG_RULES) {
            if (content.contains(rule.keyword().toLowerCase())) {
                tagScore.merge(rule.tag(), rule.weight(), Double::sum);
            }

        }
        return tagScore.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.8)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));
    }
    }
