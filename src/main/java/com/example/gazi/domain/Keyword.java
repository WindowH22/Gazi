package com.example.gazi.domain;

import com.example.gazi.domain.enums.KeywordEnum;
import com.example.gazi.domain.enums.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 교통수단 or 이슈
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordEnum keywordEnum;

    @Enumerated(EnumType.STRING)
    @Column
    private Vehicle vehicleType;
    // 이름
    @Column(nullable = false)
    private String keywordName;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE)
    private List<KeywordCart> keywordCarts = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE)
    private List<KeywordPost> keywordPosts = new ArrayList<>();
}
