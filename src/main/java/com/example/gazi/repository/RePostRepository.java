package com.example.gazi.repository;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RePostRepository extends JpaRepository<Repost, Long> {
    List<Repost> findAllByPostOrderByCreatedAtDesc(Post post);
    Page<Repost> findAllByMember(Member member, Pageable pageable);
}
