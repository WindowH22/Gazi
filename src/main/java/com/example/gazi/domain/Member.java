package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MEMBERS")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, length = 20)
    private String password;
    @Column(nullable = false, unique = true, length = 10)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Role role;
}
