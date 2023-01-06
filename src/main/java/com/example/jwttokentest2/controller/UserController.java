package com.example.jwttokentest2.controller;

import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final TokenUtil tokenUtil;

    /**
     * 로그인 요청 처리
     * @param user
     * @param response
     * @return
     */
    @GetMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request)  {
        Map<String, Object> result = tokenUtil.loginCheckToken(user, response, request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 로그아웃 요청 처리
     * @param request
     * @return
     */
    @GetMapping("logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> result = tokenUtil.logout(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Security Test(권한 : ROLE_ADMIN)
     * @param request
     * @return
     */
    @GetMapping("admin")
    public ResponseEntity<Map<String, Object>> admin(HttpServletRequest request) {
        Map<String, Object> result = tokenUtil.getToken(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Security Test(권한 : ROLE_ADMIN , ROLE_USER)
     * @param request
     * @return
     */
    @GetMapping("test")
    public ResponseEntity<Map<String, Object>> test(HttpServletRequest request) {
        Map<String, Object> result = tokenUtil.getToken(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}


