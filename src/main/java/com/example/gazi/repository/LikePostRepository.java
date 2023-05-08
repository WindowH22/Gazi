package com.example.gazi.repository;

import com.example.gazi.domain.LikePost;
import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    Optional<LikePost> findByLikeIdAndPostId(Long likeId, Long postId);

    boolean existsByLikeIdAndPostId(Long id, Long id1);

    boolean existsByLikeIdAndRepostId(Long id, Long id1);

    Optional<LikePost> findByLikeIdAndRepostId(Long likeId, Long repostId);

    Long countByRepost(Repost repost);

    Long countByPost(Post post);
}
