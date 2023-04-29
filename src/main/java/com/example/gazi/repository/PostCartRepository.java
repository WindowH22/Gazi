package com.example.gazi.repository;

import com.example.gazi.domain.Post;
import com.example.gazi.domain.PostCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCartRepository extends JpaRepository<PostCart, Long> {
    PostCart findByPost(Post post);
}
