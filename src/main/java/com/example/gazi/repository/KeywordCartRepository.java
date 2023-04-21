package com.example.gazi.repository;

import com.example.gazi.domain.KeywordCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordCartRepository extends JpaRepository<KeywordCart, Long> {
}
