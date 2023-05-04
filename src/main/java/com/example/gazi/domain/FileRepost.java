package com.example.gazi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileRepost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String fileName;
    @Column
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name="REPOST_ID")
    private Repost repost;


    public static FileRepost toEntity(String fileName, String fileUrl, Repost repost){
        FileRepost fileRepost = new FileRepost();
        fileRepost.setFileName(fileName);
        fileRepost.setFileUrl(fileUrl);
        fileRepost.setRepost(repost);
        return fileRepost;
    }
}
