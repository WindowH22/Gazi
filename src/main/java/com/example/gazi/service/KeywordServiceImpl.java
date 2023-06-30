package com.example.gazi.service;

import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.*;
import com.example.gazi.domain.enums.KeywordEnum;
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
    public ResponseEntity<Body> interestKeyword(List<Long> keywordList) {
        try{
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
            );
            List<String> existErrors = new ArrayList<>();
            for (Long keywordId : keywordList){
                Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(
                        () -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다.")
                );
                Cart cart = cartRepository.findByMemberId(member.getId());

                if(keywordCartRepository.existsByCartAndKeyword(cart,keyword)) {
                    existErrors.add(keyword.getKeywordName());

                }else{
                    KeywordCart keywordCart = KeywordCart.addKeywordCart(cart,keyword);
                    keywordCartRepository.save(keywordCart);
                }
            }

            if(existErrors.size() > 0 ){
                String msg = "";
                for (int i = 0 ; i<existErrors.size(); i++){
                    if(i != existErrors.size()-1){
                        msg += existErrors.get(i)+", ";
                    }else{
                        msg += existErrors.get(i) + "은(는) 이미 관심 키워드에 존재합니다.";
                    }
                }

                return response.fail(msg,HttpStatus.CONFLICT);
            }

            return response.success("관심 키워드가 추가되었습니다.");
        }
        catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    @Transactional
    public ResponseEntity<Body> addKeyword(RequestKeywordDto dto){
        Keyword keyword;

        if(KeywordEnum.VEHICLE.equals(dto.getKeywordEnum())){
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

    @Override
    public ResponseEntity<Body> updateInterestKeyword(RequestKeywordDto.updateKeywordDto updateKeywordDto) {
        try{
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
            );
            Cart cart = cartRepository.findByMemberId(member.getId());
            List<Long> deleteKeywordIdList = updateKeywordDto.getDeleteKeywordIdList();
            List<String> existErrors = new ArrayList<>();
            // 삭제
            for(Long keywordId : deleteKeywordIdList){
                KeywordCart keyword = keywordCartRepository.findByCartIdAndKeywordId(cart.getId(),keywordId).orElseThrow(
                        () -> new EntityNotFoundException("해당 하는 키워드가 존재하지 않습니다.")
                );
                keywordCartRepository.delete(keyword);
            }

            // 추가
            List<Long> addKeywordIdList = updateKeywordDto.getAddKeywordIdList();
            for(Long keywordId : addKeywordIdList){
                Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(
                        () -> new EntityNotFoundException("해당 키워드는 존재하지 않습니다.")
                );
                if(keywordCartRepository.existsByCartAndKeyword(cart,keyword)) {
                    existErrors.add(keyword.getKeywordName());
                }
                KeywordCart keywordCart = KeywordCart.addKeywordCart(cart,keyword);
                keywordCartRepository.save(keywordCart);
            }
            return response.success("키워드 업데이트 완료");
        }catch (Exception e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

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
                        keyword.getId(),
                        keyword.getKeywordEnum(),
                        keyword.getVehicleType(),
                        keyword.getKeywordName()
                );
            }else{
                keywordDto = new RequestKeywordDto(
                        keyword.getId(),
                        keyword.getKeywordEnum(),
                        keyword.getKeywordName()
                );
            }
            keywordCarts.add(keywordDto);
        }
        return keywordCarts;
    }

    @Override
    public ResponseEntity<Body> keywordList(){
        List<Keyword> keywordList = keywordRepository.findAll();
        return response.success(keywordList,"키워드 리스트입니다.",HttpStatus.OK);
    }

}
