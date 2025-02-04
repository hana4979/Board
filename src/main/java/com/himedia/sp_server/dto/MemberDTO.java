package com.himedia.sp_server.dto;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemberDTO extends User {
    // user 가 요구하는 생성자
    public MemberDTO(String username, String password, String name, String email, String phone, Timestamp indate, String provider, String sns_id, String sns_user, List<String> roleNames) {
        super(
                username,
                password,
                roleNames.stream().map(
                        str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList())
        );
        // authorities : 권한지정 - 조인된 어쩌구 등급 우린 안씀. 지만 null 하니 Cannot pass a null GrantedAuthority collection 에러가 남.
        this.userid = username;
        this.pwd = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.indate = indate;
        this.provider = provider;
        this.sns_id = sns_id;
        this.sns_user = sns_user;
    }

    private String userid;
    private String pwd;
    private String name;
    private String email;
    private String phone;
    private Timestamp indate;
    private String provider;
    private String sns_id;
    private String sns_user;

    /*
        jwt 토큰 생성시에 그 안에 넣을 개인 정보들을 Map 형식으로 구성
        암호화 jwt 토큰 생성시에 그 Map 을 통채로 암호화함
    */
    public Map<String, Object> getClaims(){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userid", userid);
        dataMap.put("pwd", pwd);
        dataMap.put("name", name);
        dataMap.put("email", email);
        dataMap.put("phone", phone);
        dataMap.put("indate", indate);
        dataMap.put("provider", provider);
        dataMap.put("sns_id", sns_id);
        dataMap.put("sns_user", sns_user);
        return dataMap;
    }

    // dataMap 이라는 HashMAp(상위클래스 Map) 에 전부 넣어줌 --> 통채로 암호화할 예정
}
