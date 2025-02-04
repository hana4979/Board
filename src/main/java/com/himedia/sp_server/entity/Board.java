package com.himedia.sp_server.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int num;
    private String userid;
    private String email;
    private String pass;
    //@Column(name="subject") // 본래 컬럼 이름은 subject
    private String title;
    private String content;
    private String image;
    private String savefilename;
    private int readcount;
    @CreationTimestamp
    private Timestamp writedate;
}
