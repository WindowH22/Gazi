package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.*;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
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
    public ResponseEntity<Body> addPost(RequestPostDto.addPostDto dto, List<MultipartFile> fileList, MultipartFile thumbnail) {
        Keyword headKeyword = keywordRepository.findById(dto.getHeadKeywordId()).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));


        if (!dto.getKeywordIdList().contains(dto.getHeadKeywordId())) {
            return response.fail("대표 키워드는 키워드로 선택한 값중에서 지정해야 합니다.", HttpStatus.NOT_FOUND);
        }
//        String uploadThumbnailUrl = fileService.uploadFile(thumbnail, makeFileName("thumbnail"));
        // 1.포스트 추가
//        Post post = dto.toEntity(dto.getPlaceName(), dto.getTitle(), dto.getContent(), dto.getLatitude(), dto.getLongitude(), headKeyword, uploadThumbnailUrl, member);
        Post post = dto.toEntity(dto.getPlaceName(), dto.getTitle(), dto.getContent(), dto.getLatitude(), dto.getLongitude(), headKeyword, null, member);
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

            KeywordPost keywordPost = KeywordPost.addKeywordPost(postCart, keyword);
            keywordPostRepository.save(keywordPost);
        }

        // 3. 파일추가
//        if (fileList != null) {
//            for (MultipartFile file : fileList) {
//                String fileName = makeFileName("postFile");
//                FilePost filePost = FilePost.toEntity(fileName, fileService.uploadFile(file, fileName), post);
//                filePostRepository.save(filePost);
//            }
//        }

        return response.success("글 작성을 완료했습니다.");
    }

    @Override
    public ResponseEntity<Body> addPost(RequestPostDto.addPostDto dto) {
        Keyword headKeyword = keywordRepository.findById(dto.getHeadKeywordId()).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));


        if (!dto.getKeywordIdList().contains(dto.getHeadKeywordId())) {
            return response.fail("대표 키워드는 키워드로 선택한 값중에서 지정해야 합니다.", HttpStatus.NOT_FOUND);
        }
        // 1.포스트 추가
        Post post = dto.toEntity(dto.getPlaceName(), dto.getTitle(), dto.getContent(), dto.getLatitude(), dto.getLongitude(), headKeyword, null, member);
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

            KeywordPost keywordPost = KeywordPost.addKeywordPost(postCart, keyword);
            keywordPostRepository.save(keywordPost);
        }

        return response.success(post.getId(), "글 작성을 완료했습니다.", HttpStatus.CREATED);
    }


    @Override
    @Transactional
    public ResponseEntity<Body> fileUpload(List<MultipartFile> fileList, MultipartFile thumbnail, MultipartFile backgroundMap, Long postId) {
        // 임시 방편 로직
//        Long postId =  Long.valueOf(thumbnail.getResource().getFilename());

        Post post = postRepository.getReferenceById(postId);
        String uploadThumbnailUrl = fileService.uploadFile(thumbnail, makeFileName("thumbnail"));
        post.setThumbNail(uploadThumbnailUrl);

        String uploadBackgroundMapUrl = fileService.uploadFile(backgroundMap, makeFileName("backgroundMap"));
        post.setBackgroundMap(uploadBackgroundMapUrl);

        // 3. 파일추가
        if (fileList != null) {
            for (MultipartFile file : fileList) {
                String fileName = makeFileName("postFile");
                FilePost filePost = FilePost.toEntity(fileName, fileService.uploadFile(file, fileName), post);
                filePostRepository.save(filePost);
            }
        }

        return response.success();

    }

    @Transactional
    @Override
    public ResponseEntity<Body> updatePost(Long postId, RequestPostDto.updatePostDto dto, List<MultipartFile> multipartFiles) {
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
                        String fileName = makeFileName("postFile");
                        FilePost filePost = FilePost.toEntity(fileName, fileService.uploadFile(file, fileName), post);
                        filePostRepository.save(filePost);
                    }
                }
                postRepository.save(post);
                return response.success("글 수정을 완료 했습니다.");
            } else {
                return response.fail("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Transactional
    @Override
    public ResponseEntity<Body> deletePost(Long postId) {
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
                return response.success("글 삭제를 완료 했습니다.");
            } else {
                return response.fail("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

        } catch (EntityNotFoundException e) {
            return response.fail(e.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    //글 상세보기
    public ResponseEntity<Body> getTopPost(Double curX, Double curY, Long postId, Pageable pageable) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

            List<Long> keywordIdList = new ArrayList<>();
            for (KeywordPost keywordPost : post.getPostCart().getKeywordPosts()) {
                keywordIdList.add(keywordPost.getKeyword().getId());
            }

            // 도움돼요
            Like like = likeRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException()
            );

            Long likeCount = likePostRepository.countByPost(post);

            boolean isLike = likePostRepository.existsByLikeIdAndPostId(like.getId(), post.getId());

            // 신고하기
            Report report = reportRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("신고 테이블을 찾을 수 없습니다.")
            );
            boolean isReport;

            // 답글
            List<Repost> rePosts = rePostRepository.findAllByPost(post);

            List<ResponsePostListDto> postList = new ArrayList<>();

            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("time"));

            List<ResponseFileDto> fileList = new ArrayList<>();



            for (Repost repost : rePosts) {

                List<FileRepost> fileReposts = repost.getFileRePosts();
                likeCount = likePostRepository.countByRepost(repost);
                keywordIdList.clear();
                for (KeywordRepost keywordRepost : repost.getRepostCart().getKeywordReposts()) {
                    keywordIdList.add(keywordRepost.getKeyword().getId());
                }

                isLike = likePostRepository.existsByLikeIdAndRepostId(like.getId(), repost.getId());
                // 신고하기
                isReport = reportPostRepository.existsByReportIdAndRepostId(report.getId(), repost.getId());
                System.out.println("신고 여부: " + isReport);
                fileList = new ArrayList<>();
                for (FileRepost fileRepost : fileReposts) {
                    ResponseFileDto dto = new ResponseFileDto(fileRepost.getFileName(), fileRepost.getFileUrl());
                    fileList.add(dto);
                }

                ResponsePostListDto rePostDto = ResponsePostListDto.toDto(repost, getTime(repost.getCreatedAt()), getDistance(curX, curY, repost.getLatitude(), repost.getLongitude()), fileList, likeCount, isLike, isReport, keywordIdList);
                postList.add(rePostDto);
            }

            fileList = new ArrayList<>();

            // post 파일내용
            List<FilePost> filePosts = filePostRepository.findAllByPost(post);
            for (FilePost filePost : filePosts) {

                ResponseFileDto filePostDto = new ResponseFileDto(filePost.getFileName(), filePost.getFileUrl());
                fileList.add(filePostDto);
            }

            isReport = reportPostRepository.existsByReportIdAndPostId(report.getId(), post.getId());
            postList.add(ResponsePostListDto.toDto(post, getTime(post.getCreatedAt()), getDistance(curX, curY, post.getLatitude(), post.getLongitude()), fileList, likeCount, isLike, isReport, keywordIdList));

            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), postList.size());
            Page<ResponsePostListDto> postDtoPage = new PageImpl<>(postList.subList(start, end), pageRequest, postList.size());

            // 조회수 증가
            if (post.getHit() == null) {
                post.setHit(1L);
            } else {
                post.setHit(post.getHit() + 1);
            }
            postRepository.save(post);

            ResponsePostDto.getTopPostDto responsePostDto = ResponsePostDto.getTopPostDto.toDto(post,getDistance(curX,curY,post.getLatitude(),post.getLongitude()),getTime(post.getCreatedAt()),postDtoPage);

            return response.success(responsePostDto, "상위 게시글 조회", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    // 커뮤 전체글 리스트
    public ResponseEntity<Body> getPost(Double curX, Double curY, Pageable pageable, Long keywordId) {
        // 회원인지확인
        try {
            memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Page<Post> postList;
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("time"));
            //TODO: 지연시간에 따라 자동 업데이트 쳐줘야 함

            // 전체글인지 키워드 글인지 확인
            if (keywordId != null) {
                Keyword keyword = keywordRepository.getReferenceById(keywordId);
                Page<KeywordPost> keywordPostPage = keywordPostRepository.findAllByKeyword(keyword, pageable);

                Page<ResponsePostDto.getPostDto> postDtoPage = keywordPostPage.map(m -> m.getPostCart().getPost().getRePosts().size() == 0 ?
                                ResponsePostDto.getPostDto.toDto(
                                        m.getPostCart().getPost(),
                                        getTime(m.getPostCart().getPost().getCreatedAt()),
                                        getDistance(curX, curY, m.getPostCart().getPost().getLatitude(), m.getPostCart().getPost().getLongitude()), contentSummary(m.getPostCart().getPost().getContent()))
                                :
                                ResponsePostDto.getPostDto.toDto(
                                        m.getPostCart().getPost(),
                                        getTime(m.getPostCart().getPost().getRePosts().get(m.getPostCart().getPost().getRePosts().size() - 1).getCreatedAt()),
                                        getDistance(curX, curY, m.getPostCart().getPost().getLatitude(), m.getPostCart().getPost().getLongitude()), contentSummary(m.getPostCart().getPost().getContent()))

                );

                return response.success(postDtoPage);
            }

            postList = postRepository.findAll(pageable);
            Page<ResponsePostDto.getPostDto> postDtoPage = getPostDtoPage(curX, curY, postList);

            return response.success(postDtoPage);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // 내가 작성한 글
    @Override
    public ResponseEntity<Body> getMyPost(Double curX, Double curY, Pageable pageable, Boolean isPost) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Page<ResponsePostDto.getPostDto> postDtoPage;
            Long postCount;
            if (isPost) {
                Page<Post> postList = postRepository.findAllByMember(member, pageable);
                postDtoPage = getPostDtoPage(curX, curY, postList);
                postCount = postList.getTotalElements();
                ResponsePostDto.getMyPostDto myPostDto = new ResponsePostDto.getMyPostDto(postCount, postDtoPage);
                return response.success(myPostDto);
            } else {
                // 답글 단 글
                Page<Repost> repostList = rePostRepository.findAllByMember(member, pageable);
                Page<ResponsePostDto.myRepost> repostDtoPage = repostList.map(m -> ResponsePostDto.myRepost.toDto(m));
                postCount = repostList.getTotalElements();
                ResponsePostDto.getMyRepostDto myRepostDtoList = new ResponsePostDto.getMyRepostDto(postCount, repostDtoPage);
                return response.success(myRepostDtoList);
            }


        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Body> getPostByLocation(Double minLat, Double minLon, Double maxLat, Double maxLon, Double curX, Double curY, Pageable pageable, Boolean isNearSearch) {
        try {
            memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            // 지도 내에 추출한 postData
            Page<Post> postList;
            if (isNearSearch) {
                postList = findBy5km(curX, curY, pageable);
            } else {
                postList = postRepository.findAllByLocation(minLat, minLon, maxLat, maxLon, pageable);
            }

            Page<ResponsePostDto.getPostDto> postDtoPage = getPostDtoPage(curX, curY, postList);

            return response.success(postDtoPage);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // post dto 페이지 로직
    public Page<ResponsePostDto.getPostDto> getPostDtoPage(Double curX, Double curY, Page<Post> postList) {

        Page<ResponsePostDto.getPostDto> postDtoPage = postList.map(m -> m.getRePosts().size() == 0 ?
                ResponsePostDto.getPostDto.builder()
                        .title(m.getTitle())
                        .distance(getDistance(curX, curY, m.getLatitude(), m.getLongitude()))
                        .time(getTime(m.getCreatedAt()))
                        .rePostCount(m.getRePosts().stream().count())
                        .content(contentSummary(m.getContent()))
                        .latitude(m.getLatitude())
                        .longitude(m.getLongitude())
                        .headKeyword(m.getHeadKeyword().getId())
                        .thumbNail(m.getThumbNail())
                        .postId(m.getId())
                        .backgroundMap(m.getBackgroundMap())
                        .placeName(m.getPlaceName())
                        .build()
                :
                ResponsePostDto.getPostDto.builder()
                        .title(m.getTitle())
                        .distance(getDistance(curX, curY, m.getLatitude(), m.getLongitude()))
                        .time(getTime(m.getRePosts().get(m.getRePosts().size() - 1).getCreatedAt()))
                        .rePostCount(m.getRePosts().stream().count())
                        .content(contentSummary(m.getContent()))
                        .latitude(m.getLatitude())
                        .longitude(m.getLongitude())
                        .headKeyword(m.getHeadKeyword().getId())
                        .thumbNail(m.getThumbNail())
                        .postId(m.getId())
                        .backgroundMap(m.getBackgroundMap())
                        .placeName(m.getPlaceName())
                        .build()

        );

        return postDtoPage;
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

    // 현재 위치에서 5km 찾는 로직
    public Page<Post> findBy5km(double nowLatitude, double nowLongitude, Pageable pageable) {
        final Long EARTH_RADIUS = 6371L;
        //m당 y 좌표 이동 값
        double mForLatitude = (1 / (EARTH_RADIUS * 1 * (Math.PI / 180))) / 1000;
        //m당 x 좌표 이동 값
        double mForLongitude = (1 / (EARTH_RADIUS * 1 * (Math.PI / 180) * Math.cos(Math.toRadians(nowLatitude)))) / 1000;

        //현재 위치 기준 검색 거리 좌표
        double maxY = nowLatitude + (5000L * mForLatitude);
        double minY = nowLatitude - (5000L * mForLatitude);
        double maxX = nowLongitude + (5000L * mForLongitude);
        double minX = nowLongitude - (5000L * mForLongitude);

        return postRepository.findAllByLocation(minY, minX, maxY, maxX, pageable);
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

    // 내용 축약
    public String contentSummary(String content) {
        if (content.length() > 45) {
            return content.substring(0, 46);
        } else {
            return content;
        }
    }

}
