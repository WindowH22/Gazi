package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MEMBER")
@Entity
public class Member extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private Boolean isAgree;
    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private Cart cart;
    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private Like like;
    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private Report report;

    @ToString.Exclude // exclude를 통해 tostring을 끊는다.하지않으면 순환참조가 발생한다.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Post> Post = new ArrayList<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

}
