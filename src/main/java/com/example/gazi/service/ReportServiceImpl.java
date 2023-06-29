package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.RequestReportDto;
import com.example.gazi.dto.Response;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final PostRepository postRepository;
    private final RePostRepository rePostRepository;
    private final ReportRepository reportRepository;
    private final ReportPostRepository reportPostRepository;
    private final MemberRepository memberRepository;
    private final Response response;

    @Override
    public ResponseEntity<Response.Body> ReportPost(RequestReportDto dto) {
        try {
            Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                    () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Report report = reportRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("신고 테이블을 찾을 수 없습니다.")
            );

            if(reportPostRepository.existsByReportIdAndPostId(report.getId(),post.getId())){
                return response.fail("이미 신고한 게시물입니다.",HttpStatus.UNAUTHORIZED);
            }

            ReportPost reportPost = ReportPost.addReportPost(report, post);

            reportPostRepository.save(reportPost);

            Long reportCount = reportPostRepository.countByPostId(post.getId());
            if (reportCount > 5) {
                //게시글 삭제
                postRepository.delete(post);
                return response.success("신고 횟수가 5회를 초과하여 해당게시글은 삭제되었습니다.");
            } else {
                return response.success(post.getId() + "번 게시글 신고 완료");
            }

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return response.fail("해당 게시글은 찾을 수 없습니다", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response.Body> ReportRepost(RequestReportDto dto) {
        try {
            Repost repost = rePostRepository.findById(dto.getRepostId()).orElseThrow(
                    () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
            );
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("회원을 찾을수 없습니다.")
            );

            Report report = reportRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("신고 테이블을 찾을 수 없습니다.")
            );

            if(reportPostRepository.existsByReportIdAndRepostId(report.getId(),repost.getId())){
                return response.fail("이미 신고한 게시물입니다.",HttpStatus.UNAUTHORIZED);
            }

            ReportPost reportPost = ReportPost.addReportRepost(report, repost);
            reportPostRepository.save(reportPost);

            Long reportCount = reportPostRepository.countByPostId(repost.getId());
            if (reportCount > 5) {
                //게시글 삭제
                rePostRepository.delete(repost);
                return response.success("신고 횟수가 5회를 초과하여 해당게시글은 삭제되었습니다.");
            } else {
                return response.success(repost.getId() + "번 게시글 신고 완료");
            }

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
