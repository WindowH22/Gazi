package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.RequestRepostDto;
import com.example.gazi.dto.Response;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.gazi.service.FileServiceImpl.makeFileName;

@Service
@RequiredArgsConstructor
public class RepostServiceImpl implements RepostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final RePostRepository rePostRepository;
    private final FileService fileService;
    private final FileRePostRepository fileRePostRepository;
    private final RepostCartRepository repostCartRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordRepostRepository keywordRepostRepository;
    private final FCMNotificationService fcmNotificationService;
    private final Response response;

    @Override
    public ResponseEntity<Response.Body> addRepost(RequestRepostDto.addDto dto, List<MultipartFile> fileList) {
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원이 존재하지 않습니다.")
        );


        Post post = postRepository.getReferenceById(dto.getPostId());

        Repost repost = dto.toEntity(post, dto.getContent(), member,dto.getLatitude(), dto.getLongitude(), dto.getKeywordIdList());
        rePostRepository.save(repost);

        // 키워드 카트 생성
        RepostCart repostCart = repostCartRepository.findByRepost(repost);
        if (repostCart == null) {
            repostCart = RepostCart.addCart(repost);
            repostCartRepository.save(repostCart);
        }

        // 키워드 추가
        for(Long keywordId : dto.getKeywordIdList()){
            Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

            if (keywordRepostRepository.existsByKeywordAndRepostCart(keyword, repostCart)) {
                return response.fail("키워드가 이미 존재합니다.", HttpStatus.CONFLICT);
            }

            KeywordRepost keywordRepost = KeywordRepost.addKeywordRepost(repostCart, keyword);
            keywordRepostRepository.save(keywordRepost);
        }

        // 파일 추가
        if (fileList != null) {
            if (fileList.size() > 10) {
                return response.fail("파일은 10개까지 업로드가능합니다.", HttpStatus.BAD_REQUEST);
            }

            for (MultipartFile file : fileList) {
                String fileName = makeFileName("repostFile");
                FileRepost fileRepost = FileRepost.toEntity(fileName, fileService.uploadFile(file, fileName), repost);
                fileRePostRepository.save(fileRepost);
            }
        }

        return response.success("하위 게시글 작성을 완료했습니다.");
    }

    @Override
    public ResponseEntity<Response.Body> addRepost(RequestRepostDto.addDto dto) {
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("회원이 존재하지 않습니다.")
        );

        Post post = postRepository.getReferenceById(dto.getPostId());

        Repost repost = dto.toEntity(post, dto.getContent(), member,dto.getLatitude(), dto.getLongitude(), dto.getKeywordIdList());
        rePostRepository.save(repost);

        // 키워드 카트 생성
        RepostCart repostCart = repostCartRepository.findByRepost(repost);
        if (repostCart == null) {
            repostCart = RepostCart.addCart(repost);
            repostCartRepository.save(repostCart);
        }

        // 키워드 추가
        for(Long keywordId : dto.getKeywordIdList()){
            Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

            if (keywordRepostRepository.existsByKeywordAndRepostCart(keyword, repostCart)) {
                return response.fail("키워드가 이미 존재합니다.", HttpStatus.CONFLICT);
            }

            KeywordRepost keywordRepost = KeywordRepost.addKeywordRepost(repostCart, keyword);
            keywordRepostRepository.save(keywordRepost);
        }

        // 최초게시글 작성자에게 알림
        if(post.getMember().getNotificationByRepost()){
            RequestFCMNotificationDto request = RequestFCMNotificationDto.builder()
                    .targetUserId(post.getMember().getId())
                    .title("")
                    .body("답글이 달렸어요.")
                    .build();
            fcmNotificationService.sendNotificationByToken(request);
            notificationRepository.save(Notification.toEntity(request,post.getMember(),NotificationEnum.REPOST));
        }

        return response.success(repost.getId(),"하위 게시글 작성을 완료했습니다.",HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Response.Body> fileUpload(List<MultipartFile> fileList, Long repostId){
        // 파일 추가
        Repost repost = rePostRepository.getReferenceById(repostId);
        if (fileList != null) {
            if (fileList.size() > 10) {
                return response.fail("파일은 10개까지 업로드가능합니다.", HttpStatus.BAD_REQUEST);
            }

            for (MultipartFile file : fileList) {
                String fileName = makeFileName("repostFile");
                FileRepost fileRepost = FileRepost.toEntity(fileName, fileService.uploadFile(file, fileName), repost);
                fileRePostRepository.save(fileRepost);
            }
        }
        return response.success();
    }

    @Override
    public ResponseEntity<Response.Body> updateRepost(Long RepostId, RequestRepostDto.updateDto dto, List<MultipartFile> fileList) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Repost repost = rePostRepository.getReferenceById(RepostId);

            if (repost.getMember().getId().equals(member.getId())) {
                if (dto.getContent() != null) repost.setContent(dto.getContent());
                if (dto.getDeleteFileNameList() != null) {
                    //파일 삭제
                    for (String fileName : dto.getDeleteFileNameList()) {
                        FileRepost fileRepost = fileRePostRepository.findByFileName(fileName).orElseThrow(() -> new EntityNotFoundException("삭제하려는 파일이 존재하지 않습니다."));
                        System.out.println(fileRepost.getFileName());
                        fileRePostRepository.delete(fileRepost);
                        fileService.deleteFile(fileName);
                    }
                }
                if (fileList != null) {
                    // 파일 업로드
                    for (MultipartFile file : fileList) {
                        String fileName = makeFileName("repostFile");
                        FileRepost fileRepost = FileRepost.toEntity(fileName, fileService.uploadFile(file, fileName), repost);
                        fileRePostRepository.save(fileRepost);
                    }
                }
                rePostRepository.save(repost);
                return response.success("하위 게시글 수정을 완료 했습니다.");
            } else {
                return response.fail("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteRepost(Long repostId) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Repost repost = rePostRepository.getReferenceById(repostId);

            if (repost.getMember().getId().equals(member.getId())) {
                List<FileRepost> fileReposts = fileRePostRepository.findAllByRepost(repost);
                // s3삭제
                for (FileRepost fileRepost : fileReposts) {
                    fileService.deleteFile(fileRepost.getFileName());
                }

                rePostRepository.delete(repost);
                return response.success("하위글 삭제를 완료 했습니다.");
            } else {
                return response.fail("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (EntityNotFoundException e) {
            return response.fail(e.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
