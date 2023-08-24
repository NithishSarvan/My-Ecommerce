package com.shopme.admin;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void passwordEncodeTest(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String rawPass = "nithish";
        String encode = bCryptPasswordEncoder.encode(rawPass);
        System.out.println(encode);

        boolean matches = bCryptPasswordEncoder.matches(rawPass, encode);


    }
}
