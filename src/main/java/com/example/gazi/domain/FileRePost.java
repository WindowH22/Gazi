package com.example.gazi.domain;

import jakarta.persistence.*;

@Entity
public class FileRePost extends File{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="REPOST_ID")
    private RePost rePost;


}
