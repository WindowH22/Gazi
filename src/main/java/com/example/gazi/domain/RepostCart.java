package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REPOST_CART")
@Entity
public class RepostCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_id")
    private Repost repost;

    @OneToMany(mappedBy = "repostCart", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<KeywordRepost> keywordReposts = new ArrayList<>();

    public static RepostCart addCart(Repost repost){
        RepostCart cart = new RepostCart();
        cart.setRepost(repost);
        return cart;
    }

}
