package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.RequestReportDto;
import com.example.gazi.dto.Response;
import com.example.gazi.repository.MemberRepository;
import com.example.gazi.repository.PostRepository;
import com.example.gazi.repository.ReportPostRepository;
import com.example.gazi.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final ReportPostRepository reportPostRepository;
    private final MemberRepository memberRepository;
    private final Response response;

    @Override
    public ResponseEntity<Response.Body> ReportPost(RequestReportDto dto) {
        try {
            Post post = postRepository.getReferenceById(dto.getPostId());
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

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
