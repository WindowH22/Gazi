package com.example.gazi.repository;

import com.example.gazi.domain.FileRePost;
import com.example.gazi.domain.RePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRePostRepository extends JpaRepository<FileRePost,Long> {
    List<FileRePost> findAllByRePost(RePost repost);
}
