package com.example.curatingserviceproject.service;

import com.example.curatingserviceproject.entity.SpaceMapping;
import com.example.curatingserviceproject.repository.SpaceMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpaceMappingService {

    private final SpaceMappingRepository spaceMappingRepository;

    @Transactional(readOnly = true) //읽기 전용
    public Optional<SpaceMapping> getByDisplaySiteKey(String displaySiteKey) {
        String key = displaySiteKey;
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Displaysitekey 없음");
        }
        return spaceMappingRepository.findByDisplaySiteKey(key);
    }

    //UPSERT
    @Transactional
    public SpaceMapping upsert(String displaySiteKey, String agncNM,String spaceNm, String spaceCode) {
        String key = displaySiteKey;
        String agnc = agncNM;
        String space = spaceNm;
        String code = spaceCode;

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Displaysitekey 없음");
        }
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("spacecode 없음");
        }

        Optional<SpaceMapping> opt = spaceMappingRepository.findByDisplaySiteKey(key);
        SpaceMapping entity;
        // 업데이트 부분
        if (opt.isPresent()) {
            entity = opt.get();
            entity.setAgncNm(agnc);
            entity.setSpaceNm(space);
            entity.setSpaceCode(code);
        }
        // 생성 부분
        else {
        entity = SpaceMapping.builder()
                .displaySiteKey(key)
                .agncNm(agnc)
                .spaceNm(space)
                .spaceCode(code)
                .build();
        }
        return spaceMappingRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<SpaceMapping> getAllMappings() {
        return spaceMappingRepository.findAll();
    }
}
