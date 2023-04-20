package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "AGREEMENT")
@Entity
public class Agreement {

    @Id
    private Long id;
    @Column
    private String agreementName;
    @Column
    private boolean isAgree;
    @Column
    @CreatedDate
    private Timestamp agreeDay;
    @ManyToOne
    private Member member;
}
