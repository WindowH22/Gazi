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
@Table(name = "LIKE_TABLE")
@Entity
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "like", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<LikePost> likePosts = new ArrayList<>();

    public static Like addLike(Member member) {
        Like like = new Like();
        like.setMember(member);
        return like;
    }

}
