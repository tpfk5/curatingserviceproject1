package com.example.curatingserviceproject.dto;

import com.example.curatingserviceproject.enums.TimePreference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class UserPreferenceDTO {
    private String preferredLocation;
    private TimePreference timePreference;
    private String preferredType;
}


