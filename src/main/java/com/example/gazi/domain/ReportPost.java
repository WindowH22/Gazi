package com.example.gazi.domain;

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

    public static ReportPost addReportPost(Report report, Post post) {
        ReportPost reportPost = new ReportPost();
        reportPost.setReport(report);
        reportPost.setPost(post);
        return reportPost;
    }

    public static ReportPost addReportRepost(Report report, Repost repost) {
        ReportPost reportPost = new ReportPost();
        reportPost.setReport(report);
        reportPost.setRepost(repost);
        return reportPost;
    }
}
