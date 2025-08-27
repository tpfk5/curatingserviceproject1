package com.example.curatingserviceproject.controller;

import com.example.curatingserviceproject.service.DisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DisplayAdminController {
    private final DisplayService displayService;


    @GetMapping("/refresh-displays")
    public int refreshDisplays() {
        return displayService.fetchANDSAVEDisplay().size();
    }
}
