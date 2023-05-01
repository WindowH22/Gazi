package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.Like;
import com.example.gazi.domain.LikePost;
import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.LikePostRepository;
import com.example.gazi.repository.LikeRepository;
import com.example.gazi.repository.MemberRepository;
import com.example.gazi.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikePostRepository likePostRepository;
    private final MemberRepository memberRepository;

    private final Response response;

    @Override
    public ResponseEntity<Body> likePost(RequestLikeDto dto) {

        try {
            Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 찾을수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 테이블을 찾을 수 없습니다.")
            );

            LikePost likePost = LikePost.addLikePost(like, post);

            likePostRepository.save(likePost);
            return response.success(post.getId() + "번 게시글 도움돼요 등록 완료");

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Body> deleteLikePost(RequestLikeDto dto) {
        try {
            Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 찾을수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 테이블을 찾을 수 없습니다.")
            );

            LikePost likePost = likePostRepository.findByLikeIdAndPostId(post.getId(), like.getId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 좋아요 테이블에서 찾을 수 없습니다.")
            );


            likePostRepository.delete(likePost);
            return response.success(post.getId() + "번 게시글 도움돼요 삭제 완료");

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
