package com.example.gazi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@MappedSuperclass
public class File {

    @Column
    private String fileName;
    @Column
    private String fileType;
    @Column
    private String fileUrl;
}
