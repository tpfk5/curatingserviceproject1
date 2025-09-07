package com.example.curatingserviceproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserPreferenceDTO {
    private String userName;
    private String preferredLocation;
    private  String preferredTag;
}


