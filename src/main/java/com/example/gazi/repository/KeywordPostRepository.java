package com.example.gazi.repository;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.KeywordPost;
import com.example.gazi.domain.PostCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordPostRepository extends JpaRepository<KeywordPost, Long> {

    void deleteAllByPostCart(PostCart postCart);

    boolean existsByKeywordAndPostCart(Keyword keyWord, PostCart postCart);

    Page<KeywordPost> findAllByKeywordId(Long keywordId, Pageable pageable);

    Page<KeywordPost> findAllByKeyword(Keyword keyword, Pageable pageable);
}
