package com.example.gazi.dto;

import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponsePostListDto {

    private Long postId;
    private Long userId;
    private String distance;
    private String content;
    private List<ResponseFileDto> fileList;
    private String nickName;
    private String time;
    private boolean isLike;
    private Long likeCount;
    private boolean isReport;
    private List<Long> keywordIdList;


    public static ResponsePostListDto toDto(Post post, String time, String distance, List<ResponseFileDto> fileList, Long likeCount,boolean isLike, boolean isReport,List<Long> keywordIdList){
        return ResponsePostListDto.builder()
                .userId(post.getMember().getId())
                .postId(post.getId())
                .content(post.getContent())
                .distance(distance)
                .fileList(fileList)
                .nickName(post.getMember().getNickName())
                .time(time)
                .isLike(isLike)
                .likeCount(likeCount)
                .isReport(isReport)
                .keywordIdList(keywordIdList)
                .build();
    }
    public static ResponsePostListDto toDto(Repost repost, String time, String distance, List<ResponseFileDto> fileList, Long likeCount, boolean isLike, boolean isReport, List<Long> keywordIdList){
        return ResponsePostListDto.builder()
                .userId(repost.getMember().getId())
                .postId(repost.getId())
                .content(repost.getContent())
                .distance(distance)
                .fileList(fileList)
                .nickName(repost.getMember().getNickName())
                .time(time)
                .isLike(isLike)
                .likeCount(likeCount)
                .isReport(isReport)
                .keywordIdList(keywordIdList)
                .build();
    }

}
