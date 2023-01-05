package com.example.jwttokentest2;

import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.repository.RedisRepository;
import com.example.jwttokentest2.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

@SpringBootTest
class JwtTokenTest2ApplicationTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisRepository redisRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void userInsert() {
        String id = "tjdwns132";
        String pw = new BCryptPasswordEncoder().encode("1234");
        String email = "sjchoi@naver.com";
        String name = "최성준";
        String role = "ROLE_USER";


        User user = User.builder()
                .userId(id)
                .userPw(pw)
                .userEmail(email)
                .userName(name)
                .userRole(role)
                .build();
        userRepository.save(user);
    }

    @Test
    void reidsTest() {
        String[] key = {"admin", "zz", "Authorization"};
        String test = "";

        for (String keys : key) {
            test +=  "|" + keys + "|";
        }
        System.out.println(test);

        String[] test2 = StringUtils.split(test, "|");
        String token = "";

        for (String authorization : test2) {
            if ((authorization.contains("Authorizatn"))) {
                token =  authorization;
            }
        }
        System.out.println("///////////" + token);
    }
}
