package com.example.curatingserviceproject.dto;


import com.example.curatingserviceproject.entity.UserPreference;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RecommendationScoreDTO {
    private List<DisplayCardDTO> cards;
    private UserPreference appliedPreference;
    private Map<String, DisplayCardDTO.ScoreDetail> scoreDetails;
}
