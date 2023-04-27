package com.example.gazi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GaziApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaziApplication.class, args);
    }

}
