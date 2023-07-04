package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.domain.enums.NotificationEnum;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final RePostRepository rePostRepository;
    private final NotificationRepository notificationRepository;
    private final FCMNotificationService fcmNotificationService;

    private final Response response;
    private Logger log = LoggerFactory.getLogger(getClass());

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

            if (likePostRepository.existsByLikeIdAndPostId(like.getId(), post.getId())) {
                return response.fail("이미 도움돼요 버튼을 누른 게시물입니다.", HttpStatus.UNAUTHORIZED);
            }

            LikePost likePost = LikePost.addLikePost(like, post);

            likePostRepository.save(likePost);

            //알림
            if (post.getMember().getNotificationByLike()) {
                RequestFCMNotificationDto request = RequestFCMNotificationDto.builder()
                        .targetUserId(post.getMember().getId())
                        .title("도움돼요 테스트")
                        .body("도움돼요 테스트")
                        .data(RequestFCMNotificationDto.makeMapByPost(post))
                        .build();
                fcmNotificationService.sendNotificationByToken(request);
                notificationRepository.save(Notification.toEntity(request, post.getMember(), NotificationEnum.LIKE,post.getId(),true));
            }
            return response.success(post.getId() + "번 상위 게시글 도움돼요 등록 완료");

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Body> likeRepost(RequestLikeDto dto) {

        try {
            Repost repost = rePostRepository.findById(dto.getRepostId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 찾을수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 테이블을 찾을 수 없습니다.")
            );

            if (likePostRepository.existsByLikeIdAndRepostId(like.getId(), repost.getId())) {
                return response.fail("이미 도움돼요 버튼을 누른 게시물입니다.", HttpStatus.UNAUTHORIZED);
            }

            LikePost likePost = LikePost.addLikePost(like, repost);
            likePostRepository.save(likePost);

            log.info("좋아요 동작완료");
            log.info("알림 동작시작");

            // 알림
            if (repost.getMember().getNotificationByLike()) {
                RequestFCMNotificationDto request = RequestFCMNotificationDto.builder()
                        .targetUserId(repost.getMember().getId())
                        .title("도움돼요 테스트")
                        .body("도움돼요 테스트")
                        .data(RequestFCMNotificationDto.makeMapByRepost(repost))
                        .build();
                fcmNotificationService.sendNotificationByToken(request);
                notificationRepository.save(Notification.toEntity(request, repost.getMember(), NotificationEnum.LIKE));
            }
            log.info("알림 동작완료");

            return response.success(repost.getId() + "번 하위 게시글 도움돼요 등록 완료");

        } catch (Exception e) {
            e.printStackTrace();
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

            LikePost likePost = likePostRepository.findByLikeIdAndPostId(like.getId(), post.getId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 좋아요 테이블에서 찾을 수 없습니다.")
            );


            likePostRepository.delete(likePost);
            return response.success(post.getId() + "번 상위 게시글 도움돼요 삭제 완료");

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Body> deleteLikRePost(RequestLikeDto dto) {
        try {
            Repost repost = rePostRepository.findById(dto.getRepostId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 찾을수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("좋아요 테이블을 찾을 수 없습니다.")
            );

            LikePost likePost = likePostRepository.findByLikeIdAndRepostId(like.getId(), repost.getId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 게시물을 좋아요 테이블에서 찾을 수 없습니다.")
            );

            likePostRepository.delete(likePost);
            return response.success(repost.getId() + "번 하위 게시글 도움돼요 삭제 완료");

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
