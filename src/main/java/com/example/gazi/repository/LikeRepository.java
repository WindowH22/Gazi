package com.example.gazi.repository;

import com.example.gazi.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByMemberId(Long id);
}
