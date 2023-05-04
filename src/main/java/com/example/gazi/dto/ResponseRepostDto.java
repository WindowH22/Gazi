package com.example.gazi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponseRepostDto {

    private String content;
    private List<ResponseFileRepostDto> fileRePostList;
    private String nickName;
    private String time;


}
