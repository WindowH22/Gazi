package com.example.gazi.repository;

import com.example.gazi.domain.FilePost;
import com.example.gazi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilePostRepository extends JpaRepository<FilePost, Long> {
    List<FilePost> findAllByPost(Post post);

    List<FilePost> findAllByPostId(Long id);

    Optional<FilePost> findByFileName(String fileName);
}
