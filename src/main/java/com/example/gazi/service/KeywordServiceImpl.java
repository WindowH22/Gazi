package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.dto.RequestKeywordCartDto;
import com.example.gazi.dto.RequestKeywordDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.CartRepository;
import com.example.gazi.repository.KeywordCartRepository;
import com.example.gazi.repository.KeywordRepository;
import com.example.gazi.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KeywordServiceImpl implements KeywordService{

    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final CartRepository cartRepository;
    private final KeywordCartRepository keywordCartRepository;
    private final Response response;

    //관심 키워드 등록
    @Override
    @Transactional
    public ResponseEntity<Body> interestKeyword(List<RequestKeywordCartDto> keywordCartDtoList) {

        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
        );

        for (RequestKeywordCartDto keywordCartDto : keywordCartDtoList){
            Keyword keyword = keywordRepository.findById(keywordCartDto.getKeywordId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다.")
            );
            Cart cart = cartRepository.findByMemberId(member.getId());

            if(keywordCartRepository.existsByCartAndKeyword(cart,keyword)) {
                return response.fail(keyword.getKeywordName() +"은(는) 존재합니다.", HttpStatus.BAD_REQUEST);
            }

            KeywordCart keywordCart = keywordCartDto.toEntity(cart,keyword);

            keywordCart.addKeywordCart(cart,keyword);
            keywordCartRepository.save(keywordCart);
        }

        return response.success("관심 키워드가 추가되었습니다.");
    }

    @Override
    @Transactional
    public ResponseEntity<Body> addKeyword(RequestKeywordDto dto){
        Keyword keyword;

        //유효성 검사
        if(keywordRepository.existsByKeywordName(dto.getKeywordName())){
            return response.fail(dto.getKeywordName()+"는 이미 존재하는 값입니다.",HttpStatus.CONFLICT);
        }

        if(KeywordEnum.KEYWORD_VEHICLE.equals(dto.getKeywordEnum())){
            keyword = dto.toEntity(dto.getKeywordEnum(), dto.getVehicle(), dto.getKeywordName());
        }else{
            keyword = dto.toEntity(dto.getKeywordEnum(), dto.getKeywordName());
        }
        keywordRepository.save(keyword);
        return response.success("키워드가 등록되었습니다.");


    }

    @Override
    public ResponseEntity<Body> myKeywordList() {
        return response.success(myKeyword());
    }

    public List<RequestKeywordDto> myKeyword(){
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
        );
        List<KeywordCart> findKeywordCarts = keywordCartRepository.findAllByCart(member.getCart());
        List<RequestKeywordDto> keywordCarts = new ArrayList<>();

        for(KeywordCart findKeywordCart : findKeywordCarts){
            RequestKeywordDto keywordDto;
            Keyword keyword = findKeywordCart.getKeyword();

            if(keyword.getVehicleType() != null){
                keywordDto = new RequestKeywordDto(
                        keyword.getKeywordEnum(),
                        keyword.getVehicleType(),
                        keyword.getKeywordName()
                );
            }else{
                keywordDto = new RequestKeywordDto(
                        keyword.getKeywordEnum(),
                        keyword.getKeywordName()
                );
            }
            keywordCarts.add(keywordDto);
        }
        return keywordCarts;
    }

}