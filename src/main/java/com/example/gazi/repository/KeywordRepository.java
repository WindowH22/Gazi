package com.example.gazi.repository;

import com.example.gazi.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword,Long> {
    boolean existsByKeywordName(String keywordName);
}
