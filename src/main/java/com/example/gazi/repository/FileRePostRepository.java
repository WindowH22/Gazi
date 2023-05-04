package com.example.gazi.repository;

import com.example.gazi.domain.FileRepost;
import com.example.gazi.domain.Repost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRePostRepository extends JpaRepository<FileRepost,Long> {
    List<FileRepost> findAllByRepost(Repost repost);

    Optional<FileRepost> findByFileName(String fileName);
}
