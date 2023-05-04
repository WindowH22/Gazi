package com.example.gazi.repository;

import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RePostRepository extends JpaRepository<Repost, Long> {
    Page<Repost> findAllByPost(Post post, Pageable pageable);
}
