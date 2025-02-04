package com.himedia.sp_server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SpServerApplicationTests {

    @Autowired
    PasswordEncoder pe = new BCryptPasswordEncoder();

    @Test
    void contextLoads() {
        pe.encode("1234");
        System.out.println(pe.encode("1234"));
    }

}
