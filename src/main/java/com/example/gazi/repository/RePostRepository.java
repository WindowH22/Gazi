package com.example.gazi.repository;

import com.example.gazi.domain.Post;
import com.example.gazi.domain.RePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RePostRepository extends JpaRepository<RePost, Long> {
    List<RePost> findAllByPost(Post post);
}
