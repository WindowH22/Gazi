package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "LIKE_POST")
@Entity
public class LikePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_id")
    @JsonIgnore
    private Repost repost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_id")
    private Like like;

    public static LikePost addLikePost(Like like, Post post) {
        LikePost likePost = new LikePost();
        likePost.setLike(like);
        likePost.setPost(post);
        return likePost;
    }

    public static LikePost addLikePost(Like like, Repost repost) {
        LikePost likePost = new LikePost();
        likePost.setLike(like);
        likePost.setRepost(repost);
        return likePost;
    }
}
