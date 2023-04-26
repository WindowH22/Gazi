package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.KeywordEnum;
import com.example.gazi.domain.Vehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestKeywordDto {

    private Long id;
    private KeywordEnum keywordEnum;
    private Vehicle vehicle;
    private String keywordName;

    public Keyword toEntity(KeywordEnum keywordEnum, String keywordName){
        return Keyword.builder()
                .keywordEnum(keywordEnum)
                .keywordName(keywordName)
                .build();
    }
    public Keyword toEntity(KeywordEnum keywordEnum,Vehicle vehicle, String keywordName){
        return Keyword.builder()
                .keywordEnum(keywordEnum)
                .vehicleType(vehicle)
                .keywordName(keywordName)
                .build();
    }

    public RequestKeywordDto(Long id,KeywordEnum keywordEnum, String keywordName){
        this.id = id;
        this.keywordEnum = keywordEnum;
        this.keywordName = keywordName;
    }

    public RequestKeywordDto(Long id, KeywordEnum keywordEnum, Vehicle vehicle,String keywordName){
        this.id = id;
        this.keywordEnum = keywordEnum;
        this.vehicle = vehicle;
        this.keywordName = keywordName;
    }
}
