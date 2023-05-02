package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REPORT")
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "report", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<ReportPost> reportPosts = new ArrayList<>();

    public static Report addReport(Member member){
        Report report = new Report();
        report.setMember(member);
        return report;
    }
}
