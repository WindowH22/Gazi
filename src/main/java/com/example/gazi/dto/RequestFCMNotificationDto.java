package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
public class RequestFCMNotificationDto {
    private Long targetUserId;
    private String title;
    private String body;
    private Map<String, String> data;
    private Set<Member> memberList;

    @Builder
    public RequestFCMNotificationDto(Long targetUserId, String title, String body, Set<Member> memberList,Post post,Map<String, String> data ) {
        this.targetUserId = targetUserId;
        this.title = title;
        this.body = body;
        this.memberList = memberList;
        this.data = data;
    }

    public static Map<String,String> makeMapByPost(Post post){
        Map<String,String> postMap = new HashMap<>();
        postMap.put("title",post.getTitle());
        postMap.put("repostCount",""+post.getRePosts().size()+1);
        postMap.put("time",""+post.getCreatedAt());
        postMap.put("postId","" + post.getId());

        return postMap;
    }

    public static Map<String,String> makeMapByRepost(Repost repost){
        Map<String,String> repostMap = new HashMap<>();
        repostMap.put("content",repost.getContent());
        repostMap.put("time",""+repost.getCreatedAt());
        repostMap.put("repostId","" + repost.getId());

        return repostMap;
    }
}
