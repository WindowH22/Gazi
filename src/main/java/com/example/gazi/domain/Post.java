package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Table(name = "POST")
@Entity
public class Post extends AuditingFields{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  일련번호

    @Column(nullable = false, length =100)
    private String title; // 제목

    @Lob //아주 긴 문자데이터(GB)를 저장할 수 있는 설정
    @Column(nullable = false)
    private String content; // 내용

    // 포스트 : 회원은 N : 1 관계다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"post"}) // 무한참조 방지
    @OrderBy("id desc") // 내림차순;
    private List<Reply> replys;
}
