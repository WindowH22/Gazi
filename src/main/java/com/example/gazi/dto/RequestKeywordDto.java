package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.KeywordEnum;
import com.example.gazi.domain.Vehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestKeywordDto {

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

    public RequestKeywordDto(KeywordEnum keywordEnum, String keywordName){
        this.keywordEnum = keywordEnum;
        this.keywordName = keywordName;
    }

    public RequestKeywordDto(KeywordEnum keywordEnum, Vehicle vehicle,String keywordName){
        this.keywordEnum = keywordEnum;
        this.vehicle = vehicle;
        this.keywordName = keywordName;
    }
}
