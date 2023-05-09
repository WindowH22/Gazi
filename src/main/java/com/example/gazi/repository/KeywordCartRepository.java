package com.example.gazi.repository;

import com.example.gazi.domain.Cart;
import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.KeywordCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordCartRepository extends JpaRepository<KeywordCart, Long> {
    List<KeywordCart> findAllByCart(Cart cart);

    Boolean existsByCartAndKeyword(Cart cart, Keyword keyword);

    Optional<KeywordCart> findByCartIdAndKeywordId(Long id, Long keywordid);
}
