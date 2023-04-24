package com.example.gazi.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "KEYWORD")
@Entity
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // 교통수단 or 이슈
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordEnum keywordEnum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Vehicle vehicleType;
    // 이름
    @Column(nullable = false)
    private String keywordName;
    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE)
    private List<KeywordCart> keywordCarts = new ArrayList<>();
}
