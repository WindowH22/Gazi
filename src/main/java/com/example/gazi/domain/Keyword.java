package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "KEYWORD")
@Entity
public class Keyword {
    @Id
    private Long id;
    // 교통수단 or 이슈
    @Enumerated(EnumType.STRING)
    private KeywordEnum keywordEnum;
    // 이름
    @Column
    private String keywordName;

    @ManyToOne
    private Member member;
}
