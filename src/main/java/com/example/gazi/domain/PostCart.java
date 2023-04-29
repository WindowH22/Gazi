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
@Table(name = "POST_CART")
@Entity
public class PostCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "postCart", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<KeywordPost> keywordPosts = new ArrayList<>();

    public static PostCart addCart(Post post){
        PostCart cart = new PostCart();
        cart.setPost(post);
        return cart;
    }

}
