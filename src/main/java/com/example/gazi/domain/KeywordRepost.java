package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "KEYWORD_REPOST")
@Entity
public class KeywordRepost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    @JsonIgnore
    private Keyword keyword;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_cart_id")
    private RepostCart repostCart;

    public static KeywordRepost addKeywordRepost(RepostCart repostCart, Keyword keyword) {
        KeywordRepost keywordRepost = new KeywordRepost();
        keywordRepost.setRepostCart(repostCart);
        keywordRepost.setKeyword(keyword);
        return keywordRepost;
    }
}
