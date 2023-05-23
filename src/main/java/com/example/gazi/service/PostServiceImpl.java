package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.*;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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

            boolean isLike;

            // 신고하기
            Report report = reportRepository.findByMemberId(member.getId()).orElseThrow(
                    () -> new EntityNotFoundException("신고 테이블을 찾을 수 없습니다.")
            );
            boolean isReport;

            // 답글
            List<Repost> rePosts = rePostRepository.findAllByPostOrderByCreatedAtDesc(post);

            List<ResponsePostListDto> postList = new ArrayList<>();

            List<ResponseFileDto> fileList;

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
            isLike = likePostRepository.existsByLikeIdAndPostId(like.getId(), post.getId());

            postList.add(ResponsePostListDto.toDto(post, getTime(post.getCreatedAt()), getDistance(curX, curY, post.getLatitude(), post.getLongitude()), fileList, likeCount, isLike, isReport, keywordIdList));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), postList.size());
            Page<ResponsePostListDto> postDtoPage = new PageImpl<>(postList.subList(start, end), pageable, postList.size());

            // 조회수 증가
            if (post.getHit() == null) {
                post.setHit(1L);
            } else {
                post.setHit(post.getHit() + 1);
            }
            postRepository.save(post);

            ResponsePostDto.getTopPostDto responsePostDto = ResponsePostDto.getTopPostDto.toDto(post, getDistance(curX, curY, post.getLatitude(), post.getLongitude()), getTime(post.getCreatedAt()), postDtoPage);

            return response.success(responsePostDto, "상위 게시글 조회", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    // 커뮤 전체글 리스트
    public ResponseEntity<Body> getPost(Double curX, Double curY, Pageable pageable, Long keywordId) throws IOException, ParseException {
        // 회원인지확인
        try {
            memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            Page<Post> postList;
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("time"));
            //TODO: 지연시간에 따라 자동 업데이트 쳐줘야 함

//            autoAddPost();
//            System.out.println("자동 업로드는 완료");

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
        } catch (EntityNotFoundException e) {
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


        } catch (EntityNotFoundException e) {
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

        Page<ResponsePostDto.getPostDto> postDtoPage = postList.map(m -> m.getRePosts() == null ?
                ResponsePostDto.getPostDto.builder()
                        .title(m.getTitle())
                        .distance(getDistance(curX, curY, m.getLatitude(), m.getLongitude()))
                        .time(getTime(m.getCreatedAt()))
                        .rePostCount(1L)
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
                m.getRePosts().size() == 0 ?
                        ResponsePostDto.getPostDto.builder()
                                .title(m.getTitle())
                                .distance(getDistance(curX, curY, m.getLatitude(), m.getLongitude()))
                                .time(getTime(m.getCreatedAt()))
                                .rePostCount(1L)
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
                                .rePostCount(m.getRePosts().size() + 1L)
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
        if (lat2 == 0.0 && lon2 == 0.0) {
            return "거리를 측정할 수 없습니다.";
        }

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


    @Override
    public void autoAddPost() throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); /*URL*/
        urlBuilder.append("/" + URLEncoder.encode("796b737374646c6334387461504273", "UTF-8")); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
        urlBuilder.append("/" + URLEncoder.encode("xml", "UTF-8")); /*요청파일타입 (xml,xmlf,xls,json) */
        urlBuilder.append("/" + URLEncoder.encode("AccInfo", "UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
        urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
        // 상위 5개는 필수적으로 순서바꾸지 않고 호출해야 합니다.


        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");
        System.out.println("Response code: " + conn.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다.*/
        BufferedReader rd;

        // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        String xml = sb.toString();
        JSONObject json = XML.toJSONObject(xml);

        JSONObject accInfo = json.getJSONObject("AccInfo");
        JSONArray row = accInfo.getJSONArray("row");


        Map<String, String> accCode = new HashMap<>();
        accCode.put("A01", "교통사고");
        accCode.put("A02", "차량고장");
        accCode.put("A03", "보행사고");
        accCode.put("A04", "공사");
        accCode.put("A05", "낙하물");
        accCode.put("A06", "버스사고");
        accCode.put("A07", "지하철사고");
        accCode.put("A08", "화재");
        accCode.put("A09", "기상/재난");
        accCode.put("A10", "집회및행사");
        accCode.put("A11", "기타");
        accCode.put("A12", "제보");
        accCode.put("A13", "단순정보");


        Map<String, String> accDCode = new HashMap<>();
        accDCode.put("01B01", "추돌사고");
        accDCode.put("01B03", "전복사고");
        accDCode.put("01B04", "차량화재");
        accDCode.put("01B05", "차량고장");
        accDCode.put("02B01", "차량고장");
        accDCode.put("03B01", "보행사고");
        accDCode.put("04B01", "시설물보수");
        accDCode.put("04B02", "청소작업");
        accDCode.put("04B03", "차선도색");
        accDCode.put("04B04", "도로보수");
        accDCode.put("04B05", "제설작업");
        accDCode.put("04B06", "포장공사");
        accDCode.put("04B07", "가로수정비");
        accDCode.put("05B01", "소형낙하물");
        accDCode.put("05B02", "대형낙하물");
        accDCode.put("06B01", "버스사고");
        accDCode.put("07B01", "지하철사고");
        accDCode.put("08B01", "화재");
        accDCode.put("09B01", "폭우");
        accDCode.put("09B02", "호우주의보");
        accDCode.put("09B03", "호우경보");
        accDCode.put("09B04", "태풍주의보");
        accDCode.put("09B05", "태풍경보");
        accDCode.put("09B06", "폭설");
        accDCode.put("09B07", "대설주의보");
        accDCode.put("09B08", "대설경보");
        accDCode.put("09B09", "폭염");
        accDCode.put("09B10", "폭염주의보");
        accDCode.put("09B11", "한파");
        accDCode.put("09B12", "한파주의보");
        accDCode.put("09B13", "우박");
        accDCode.put("09B14", "노면미끄러움");
        accDCode.put("09B15", "도로침하");
        accDCode.put("09B16", "도로침수");
        accDCode.put("09B17", "도로결빙");
        accDCode.put("09B18", "노면패임");
        accDCode.put("09B19", "강우통제");
        accDCode.put("10B01", "훈련");
        accDCode.put("10B02", "집회/시위");
        accDCode.put("10B03", "행사");
        accDCode.put("11B01", "기타");
        accDCode.put("12B01", "제보");
        accDCode.put("13B01", "단순정보");

        for (int i = 0; i < row.length(); i++) {
            Long accId = Long.parseLong(row.getJSONObject(i).get("acc_id").toString());
            if (!postRepository.existsByAccId(accId)) {
                String title; // 제목
                String placeName = ""; //장소명
                StringBuilder content = new StringBuilder();
                String thumbNail = "";
                Double latitude = 0.0;
                Double longitude = 0.0;
                List<Long> keywordIdList = null;
                Long headKeywordId = null;
                LocalDateTime createdAt;

                String accType = accCode.get(row.getJSONObject(i).get("acc_type").toString());
                String accDType = accDCode.get(row.getJSONObject(i).get("acc_dtype"));

                //제목
                title =  accType + " (으)로 인한 " + parseRodeCode(row.getJSONObject(i).get("acc_road_code").toString());

                // 장소명

                // 위도 경도,

                // 키워드 리스트

                // 대표키워드
                switch (row.getJSONObject(i).get("acc_type").toString()) {
                    case "A01", "A03", "A06", "A07", "A08":
                        headKeywordId = 1L;
                        break;
                    case "A02", "A11", "A12", "A13", "A05":
                        headKeywordId = 9L;
                        break;
                    case "A04":
                        headKeywordId = 4L;
                        break;
                    case "A09":
                        headKeywordId = 3L;
                        break;
                    case "A10":
                        headKeywordId = 8L;
                        break;


                }
                if (row.getJSONObject(i).get("acc_type").toString().equals("A01") || row.getJSONObject(i).get("acc_type").toString().equals("A03") || row.getJSONObject(i).get("acc_type").toString().equals("A06") || row.getJSONObject(i).get("acc_type").toString().equals("A07") || row.getJSONObject(i).get("acc_type").toString().equals("A08")) {
                    headKeywordId = 1L; // 사고
                } else if (row.getJSONObject(i).get("acc_type").toString().equals("A02") || row.getJSONObject(i).get("acc_type").toString().equals("A05") || row.getJSONObject(i).get("acc_type").toString().equals("A11") || row.getJSONObject(i).get("acc_type").toString().equals("A12") || row.getJSONObject(i).get("acc_type").toString().equals("A13")) {
                    headKeywordId = 9L; // 기타
                } else if (row.getJSONObject(i).get("acc_type").toString().equals("A06"))

                // content 입력
                content.append("안녕하세요, \"가늘길 지금\" 팀 입니다.\n");
                content.append(accType + " (으)로 인한 " + parseRodeCode(row.getJSONObject(i).get("acc_road_code").toString()) + "가 있을 예정입니다.\n");
                content.append("하기 내용을 바탕으로 교통편 이용 혹은 통행에 참고 바랍니다.\n");
                content.append("\n");

                content.append("- 기간: " + "발생날짜: " + parseDate(row.getJSONObject(i).get("occr_date").toString()) + " " + parseWeek(row.getJSONObject(i).get("occr_time").toString())
                        + " - 종료날짜: " + parseDate(row.getJSONObject(i).get("exp_clr_date").toString()) + " " + parseWeek(row.getJSONObject(i).get("exp_clr_time").toString()) + "\n");

//                content.append("- 위치: {위치}\n");
                content.append("- 사유: " + accType + " / " + accDType + "\n");
                String info = row.getJSONObject(i).get("acc_info").toString().replaceAll("\r", " ");
                content.append(info);

                // 게시글


                RequestPostDto.addPostDto dto = new RequestPostDto.addPostDto();

                dto.setTitle(title);
                dto.setContent(content.toString());
                dto.setHeadKeywordId(headKeywordId);
                dto.setAccId(accId);
                dto.setLatitude(latitude);
                dto.setLongitude(longitude);

                System.out.println(dto.getHeadKeywordId());
                Keyword headKeyword = keywordRepository.findById(dto.getHeadKeywordId()).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

                Member member = memberRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));

                // 1.포스트 추가
                Post post = dto.autoToEntity(dto.getPlaceName(), dto.getTitle(), dto.getContent(), dto.getLatitude(), dto.getLongitude(), headKeyword, null, member, accId);
                postRepository.save(post);
                System.out.println(post.getContent());

                System.out.println("size : " + post.getRePosts());
                // 포스트 생성과 동시에 포스트 키워드 카트 생성
                PostCart postCart = postCartRepository.findByPost(post);
                if (postCart == null) {
                    postCart = PostCart.addCart(post);
                    postCartRepository.save(postCart);
                }

                System.out.println(dto.getKeywordIdList());

                // 2.키워드 추가
                if (dto.getKeywordIdList() != null) {
                    for (Long keywordId : dto.getKeywordIdList()) {

                        Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(() -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다."));

                        KeywordPost keywordPost = KeywordPost.addKeywordPost(postCart, keyword);
                        keywordPostRepository.save(keywordPost);
                    }
                }

            }

        }
    }

    public static String parseDate(String date) throws ParseException {
        SimpleDateFormat input = new SimpleDateFormat("yyyyMMdd");  //dt와 형식을 맞추어 준다.
        SimpleDateFormat output = new SimpleDateFormat("yyyy년 MM월 dd일"); //변환할 형식
        Date newDate = input.parse(date);        //date 자료형으로 변환
        return output.format(newDate);    //date 타입을 string 으로 변환
    }

    public static String parseWeek(String time) throws ParseException {
        SimpleDateFormat input = new SimpleDateFormat("HHMM");  //dt와 형식을 맞추어 준다.
        SimpleDateFormat output = new SimpleDateFormat("HH시 MM분"); //변환할 형식
        Date newTime = input.parse(time);        //date 자료형으로 변환
        return output.format(newTime);    //date 타입을 string 으로 변환
    }

    public static String parseRodeCode(String code) {
        if (code.equals("010")) {
            return "부분 통제";
        } else if (code.equals("009")) {
            return "전면 통제";
        } else {
            return "알수없음";
        }
    }
}
