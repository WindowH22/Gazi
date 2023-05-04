package com.example.gazi.repository;

import com.example.gazi.domain.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    Long countByPostId(Long postId);

    boolean existsByReportIdAndPostId(Long reportId, Long postId);

    boolean existsByReportIdAndRepostId(Long reportId, Long repostId);
}
