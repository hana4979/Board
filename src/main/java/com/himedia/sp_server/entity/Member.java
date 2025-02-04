package com.himedia.sp_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class Member {
    @Id
    private String userid;
    private String pwd;
    private String name;
    private String email;
    private String phone;

    @CreationTimestamp
    private Timestamp indate;
    private String provider;
    private String sns_id;

    @ColumnDefault("'F'")
    private String sns_user;
}
