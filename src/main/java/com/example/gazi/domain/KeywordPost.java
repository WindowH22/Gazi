package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "KEYWORD_POST")
@Entity
public class KeywordPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    @JsonIgnore
    private Keyword keyword;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_cart_id")
    private PostCart postCart;

    public static KeywordPost addKeywordPost(PostCart postCart, Keyword keyword) {
        KeywordPost keywordPost = new KeywordPost();
        keywordPost.setPostCart(postCart);
        keywordPost.setKeyword(keyword);
        return keywordPost;
    }
}
