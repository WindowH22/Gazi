package com.example.gazi.service;

import com.example.gazi.dto.Response;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService{

    private final RedisTemplate redisTemplate;
    private final RedisUtilService redisUtilService;
    private final JavaMailSender emailSender;
    private final MemberService memberService;
    private final Response response;

    private MimeMessage createMessage(String to,String keyValue)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+keyValue);
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("가는길 지금 이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px; color=#000000'>";
        msgg+= "<h1> 이메일 인증 </h1>";
        msgg+= "<p> 안녕하세요 가는길지금 Gazi 입니다.";
        msgg+= "<br>";
        msgg+= "아래 번호를 인증번호 입력란에 입력 후 회원가입을 완료해주세요.</p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid white; font-family:verdana; background-color: #F2EBFF';>";
        msgg+= "<div style='font-size:300%; color: #8446E7'>";
        msgg+= keyValue;
        msgg+= "</div>";
        msgg+= "</div>";
        msgg+= "<hr>";
        msgg+= "<p><span style='color:#323232; font-weight:bold'>가는길지금에 가입하신 적이 없다면, 이 메일을 무시하세요.</span> <br> ";
        msgg+= "<span style='color:#9D9D9D'>본 메일은 발신 전용으로 문의에 대한 회신이 되지 않습니다. 궁금한 사항은 gazinowcs@gmail.com로 문의 부탁드립니다.</span></p>";


        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gazinowcs@gmail.com","gazi"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) { // 인증코드 8자리
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }


    @Override
    public ResponseEntity<Response.Body> sendSimpleMessage(String to) throws Exception {
        Response.Body chekEmailBody = memberService.checkEmail(to).getBody();
        if(chekEmailBody.getResult().equals("fail")){
            return response.fail(chekEmailBody.getMessage(), HttpStatus.CONFLICT);
        }
        String keyValue = createKey();
        MimeMessage message = createMessage(to,keyValue);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        if(redisUtilService.getData(to) == null){
            //유효시간 5분
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }else{
            redisTemplate.delete(to);
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }

        return response.success(keyValue,"인증번호를 발송하였습니다.", HttpStatus.OK);
    }

}
