package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "POST")
@Entity
public class Post extends AuditingFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  일련번호

    @Column(nullable = false, length =100)
    private String title; // 제목

    @Lob //아주 긴 문자데이터(GB)를 저장할 수 있는 설정
    @Column(nullable = false)
    private String content; // 내용

    @Column
    private Long hit; // 조회수

    @Column
    private Double latitude; // 위도

    @Column
    private Double longitude; // 경도

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"post"}) // 무한참조 방지
    @OrderBy("id desc") // 내림차순;
    private List<FilePost> filePosts;

    @Column
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<KeywordPost> keywordPosts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword; // 대표 키워드

    // 포스트 : 회원은 N : 1 관계다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"post"}) // 무한참조 방지
    @OrderBy("id desc") // 내림차순;
    private List<RePost> rePosts;
}
