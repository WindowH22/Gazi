package com.example.gazi.repository;

import com.example.gazi.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepostRepository extends JpaRepository<KeywordRepost, Long> {
    boolean existsByKeywordAndRepostCart(Keyword keyword, RepostCart repostCart);
}
