package com.example.gazi.domain;

import com.example.gazi.domain.enums.ReportEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "REPORT_POST")
@Entity
public class ReportPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportEnum reportEnum;
    private String reason;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_id")
    @JsonIgnore
    private Repost repost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    public static ReportPost addReportPost(Report report, Post post, ReportEnum reportEnum, String reason) {
        ReportPost reportPost = new ReportPost();
        reportPost.setReport(report);
        reportPost.setPost(post);
        reportPost.setReportEnum(reportEnum);
        reportPost.setReason(reason);
        return reportPost;
    }

    public static ReportPost addReportRepost(Report report, Repost repost, ReportEnum reportEnum, String reason) {
        ReportPost reportPost = new ReportPost();
        reportPost.setReport(report);
        reportPost.setRepost(repost);
        reportPost.setReportEnum(reportEnum);
        reportPost.setReason(reason);
        return reportPost;
    }
}
