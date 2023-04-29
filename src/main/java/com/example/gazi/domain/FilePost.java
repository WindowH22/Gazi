package com.example.gazi.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "FILE_POST")
@Entity
public class FilePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    @Column
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;


    public static FilePost toEntity(String fileName, String fileUrl, Post post) {
        FilePost filePost = new FilePost();
        filePost.setFileName(fileName);
        filePost.setFileUrl(fileUrl);
        filePost.setPost(post);
        return filePost;
    }
}
