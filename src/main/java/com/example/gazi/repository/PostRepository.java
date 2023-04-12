package com.example.gazi.repository;

import com.example.gazi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Long, Post> {
}
