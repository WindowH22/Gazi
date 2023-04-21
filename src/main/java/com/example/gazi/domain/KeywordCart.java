package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "KEYWORD_CART")
@Entity
public class KeywordCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public static KeywordCart addKeywordCart(Cart cart, Keyword keyword) {
        KeywordCart keywordCart = new KeywordCart();
        keywordCart.setCart(cart);
        keywordCart.setKeyword(keyword);
        return keywordCart;
    }
}
