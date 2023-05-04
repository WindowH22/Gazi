package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.*;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.gazi.service.FileServiceImpl.makeFileName;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final PostRepository postRepository;
    private final KeywordPostRepository keywordPostRepository;
    private final FilePostRepository filePostRepository;
    private final FileRePostRepository fileRePostRepository;
    private final LikePostRepository likePostRepository;
    private final LikeRepository likeRepository;
    private final PostCartRepository postCartRepository;
    private final ReportRepository reportRepository;
    private final ReportPostRepository reportPostRepository;
    private final RePostRepository rePostRepository;
    private final Response response;
    private final FileService fileService;
    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public ResponseEntity<Response.Body> addPost(RequestPostDto.addPostDto dto, List<MultipartFile> fileList, MultipartFile thumbnail) {
        Keyword headKeyword = keywordRepository.findById(dto.getHeadKeywordId()).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));


        if (!dto.getKeywordIdList().contains(dto.getHeadKeywordId())) {
            return response.fail("대표 키워드는 키워드로 선택한 값중에서 지정해야 합니다.", HttpStatus.NOT_FOUND);
        }
        String uploadThumbnailUrl = fileService.uploadFile(thumbnail, makeFileName("thumbnail"));
        // 1.포스트 추가
        Post post = dto.toEntity(dto.getPlaceName(), dto.getTitle(), dto.getContent(), dto.getLatitude(), dto.getLongitude(), headKeyword, uploadThumbnailUrl, member);
        postRepository.save(post);

        // 포스트 생성과 동시에 포스트 키워드 카트 생성
        PostCart postCart = postCartRepository.findByPost(post);
        if (postCart == null) {
            postCart = PostCart.addCart(post);
            postCartRepository.save(postCart);
        }

        // 2.키워드 추가
        for (Long keywordId : dto.getKeywordIdList()) {

            Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

            if (keywordPostRepository.existsByKeywordAndPostCart(keyword, postCart)) {
                return response.fail("키워드가 이미 존재합니다.", HttpStatus.CONFLICT);
            }

            log.info(keyword.getId() + "");
            KeywordPost keywordPost = KeywordPost.addKeywordPost(postCart, keyword);

            keywordPostRepository.save(keywordPost);
        }

        // 3. 파일추가
        if (fileList != null) {
            for (MultipartFile file : fileList) {
                String fileName = makeFileName("postFile");
                FilePost filePost = FilePost.toEntity(fileName, fileService.uploadFile(file, makeFileName("postFile")), post);
                filePostRepository.save(filePost);
            }
        }

        return response.success("글 작성을 완료했습니다.");
    }

    @Transactional
    @Override
    public ResponseEntity<Response.Body> updatePost(Long postId, RequestPostDto.updatePostDto dto, List<MultipartFile> multipartFiles) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            Post post = postRepository.getReferenceById(postId);
            PostCart postCart = postCartRepository.findByPost(post);


            if (post.getMember().getId().equals(member.getId())) {
                if (dto.getTitle() != null) post.setTitle(dto.getTitle());
                if (dto.getPlaceName() != null) post.setPlaceName(dto.getPlaceName());
                if (dto.getContent() != null) post.setContent(dto.getContent());
                if (dto.getKeywordIdList() != null) {
                    if (dto.getKeywordIdList().size() > 0) {
                        keywordPostRepository.deleteAllByPostCart(post.getPostCart());
                        for (Long keywordId : dto.getKeywordIdList()) {
                            Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

                            if (keywordPostRepository.existsByKeywordAndPostCart(keyword, postCart)) {
                                return response.fail("키워드가 이미 존재합니다.", HttpStatus.CONFLICT);
                            }

                            KeywordPost keywordPost = KeywordPost.addKeywordPost(postCart, keyword);

                            keywordPostRepository.save(keywordPost);
                        }
                    } else {
                        return response.fail("키워드가 한개 이상은 있어야합니다.", HttpStatus.BAD_REQUEST);
                    }
                }
                if (dto.getHeadKeywordId() != null) {
                    Keyword headKeyword = keywordRepository.findById(dto.getHeadKeywordId()).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));
                    post.setHeadKeyword(headKeyword);
                }


                if (dto.getDeleteFileNameList() != null) {
                    //파일 삭제
                    for (String fileName : dto.getDeleteFileNameList()) {
                        FilePost filePost = filePostRepository.findByFileName(fileName).orElseThrow(() -> new EntityNotFoundException("삭제하려는 파일이 존재하지 않습니다."));
                        filePostRepository.delete(filePost);
                        fileService.deleteFile(fileName);
                    }
                }

                if (multipartFiles != null) {
                    // 파일 업로드
                    for (MultipartFile file : multipartFiles) {
                        LocalDateTime date = LocalDateTime.now();
                        int randomNum = (int) (Math.random() * 100);
                        String fileName = randomNum + file.getOriginalFilename() + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        fileService.uploadFile(file, "postFile");
                    }
                }

                postRepository.save(post);
            } else {
                return response.fail("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
            return response.success("글 수정을 완료 했습니다.");
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Transactional
    @Override
    public ResponseEntity<Response.Body> deletePost(Long postId) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Post post = postRepository.getReferenceById(postId);

            if (post.getMember().getId().equals(member.getId())) {
                List<FilePost> filePosts = filePostRepository.findAllByPostId(post.getId());
                //s3 삭제
                for (FilePost filePost : filePosts) {
                    fileService.deleteFile(filePost.getFileName());

                }
                postRepository.delete(post);
                log.info("요기");


            } else {
                return response.fail("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

            return response.success("글 삭제를 완료 했습니다.");
        } catch (EntityNotFoundException e) {
            return response.fail(e.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity<Response.Body> getTopPost(Long postId) {
        return null;
    }

    @Override
    public ResponseEntity<Response.Body> getTopPost(Long postId, Pageable pageable) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));
            Page<Repost> rePosts = rePostRepository.findAllByPost(post, pageable);
            List<FilePost> filePosts = filePostRepository.findAllByPost(post);

            List<ResponseRepostDto> rePostList = new ArrayList<>();
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            for (Repost repost : rePosts) {
                List<FileRepost> fileReposts = repost.getFileRePosts();
                List<ResponseFileRepostDto> fileList = new ArrayList<>();

                for (FileRepost fileRepost : fileReposts) {
                    ResponseFileRepostDto dto = new ResponseFileRepostDto(fileRepost.getFileName(), fileRepost.getFileUrl());
                    fileList.add(dto);
                }
                ResponseRepostDto rePostDto = new ResponseRepostDto(repost.getContent(), fileList, repost.getMember().getNickName(), getTime(repost.getCreatedAt()));
                rePostList.add(rePostDto);
            }
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), rePostList.size());
            Page<ResponseRepostDto> postDtoPage = new PageImpl<>(rePostList.subList(start, end), pageRequest, rePostList.size());


            List<ResponseFilePostDto> fileList = new ArrayList<>();
            for (FilePost filePost : filePosts) {
                ResponseFilePostDto filePostDto = new ResponseFilePostDto(filePost.getFileName(), filePost.getFileUrl());
                fileList.add(filePostDto);
            }

            List<Long> keywordIdList = new ArrayList<>();
            for (KeywordPost keywordPost : post.getPostCart().getKeywordPosts()) {
                keywordIdList.add(keywordPost.getKeyword().getId());
            }

            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException()
            );
            boolean isLike = likePostRepository.existsByLikeIdAndPostId(like.getId(), post.getId());

            Report report = reportRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("신고 테이블을 찾을 수 없습니다.")
            );

            boolean isReport = reportPostRepository.existsByReportIdAndPostId(report.getId(), post.getId());

            // 조회수 증가
            if (post.getHit() == null) {
                post.setHit(1L);
            } else {
                post.setHit(post.getHit() + 1);
            }
            postRepository.save(post);

            ResponsePostDto.getTopPostDto responsePostDto = new ResponsePostDto.getTopPostDto(post.getMember().getId(), post.getTitle(), post.getPlaceName(), post.getContent(), keywordIdList, post.getHeadKeyword().getId(), fileList, postDtoPage, post.getMember().getCreatedAt(), post.getMember().getNickName(), post.getHit(), post.getMember().getId(), isLike, isReport, post.getThumbNail());

            return response.success(responsePostDto, "상위 게시글 조회", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Response.Body> getPost() {
        // 회원인지확인
        try {
            memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            List<Post> postList = postRepository.findAll();
            List<ResponsePostDto.getTopPostDto> postDtoList = new ArrayList<>();
//
//            for (Post post: postList){
//                ResponsePostDto.getTopPostDto responsePostDto = new ResponsePostDto.getTopPostDto(post.getTitle(), post.getPlaceName(), post.getContent(), keywordIdList, post.getHeadKeyword().getId(), fileList, rePosts, post.getMember().getCreatedAt(), post.getMember().getNickName(), post.getHit(),post.getMember().getId(),isLike,isReport);
//            }

            return response.success(postList);

        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Response.Body> getPostByLocation(Double minLat, Double minLon, Double maxLat, Double maxLon, Double curX, Double curY, Pageable pageable) {
        try {
            memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            // 지도 내에 추출한 postData
            Page<Post> postList = postRepository.findAllByLocation(minLat, minLon, maxLat, maxLon, pageable);
            System.out.println(postList.getTotalPages());


            List<ResponsePostDto.getPostDto> postDtoList = new ArrayList<>();
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());


            for (Post post : postList) {
                ResponsePostDto.getPostDto postDto = ResponsePostDto.getPostDto.toDto(post, getTime(post.getCreatedAt()), getDistance(curX, curY, post.getLatitude(), post.getLongitude()));
                postDtoList.add(postDto);
            }
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), postDtoList.size());
            Page<ResponsePostDto.getPostDto> postDtoPage = new PageImpl<>(postDtoList.subList(start, end), pageRequest, postDtoList.size());

            return response.success(postDtoPage);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // 거리 구하기 로직
    public String getDistance(double lat1, double lon1, double lat2, double lon2) {
        final Long EARTH_RADIUS = 6371L;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS * c * 1000;    // Distance in m
        String distance;

        if (d > 1000L) {
            d = d / 1000;
            distance = (int) d + "Km";
        } else {
            distance = (int) d + "m";
        }
        return distance;
    }

    // 시간 구하기 로직
    private String getTime(LocalDateTime writeTime) {

        LocalDateTime nowDate = LocalDateTime.now();
        Duration duration = Duration.between(writeTime, nowDate);
        Long time = duration.getSeconds();
        String formatTime;

        if (time > 60 && time <= 3600) {
            // 분
            time = time / 60;
            formatTime = time + "분 전";
        } else if (time > 3600 && time <= 86400) {
            time = time / (60 * 60);
            formatTime = time + "시간 전";
        } else if (time > 86400) {
            time = time / 86400;
            formatTime = time + "일 전";
        } else {
            formatTime = time + "초 전";
        }

        return formatTime;
    }
}
