package com.example.gazi.dto;

import com.example.gazi.domain.Cart;
import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.KeywordCart;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestKeywordCartDto {

    private List<Long> myKeywordList;

    public KeywordCart toEntity(Cart cart, Keyword keyword){
        return KeywordCart.builder()
                .cart(cart)
                .keyword(keyword)
                .build();
    }
}
