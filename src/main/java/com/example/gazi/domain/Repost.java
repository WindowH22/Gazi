package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = " REPOST")
@Entity
public class Repost extends AuditingFields{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private String content;

    @OneToMany(mappedBy = "repost", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"rePost"}) // 무한참조 방지
    @OrderBy("id desc") // 내림차순;
    private List<FileRepost> fileRePosts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="POST_ID")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
