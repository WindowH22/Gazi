package com.example.gazi.service;

import com.example.gazi.dto.Response;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    ResponseEntity<Response.Body> sendSimpleMessage(String to)throws Exception;
}
