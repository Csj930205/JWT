package com.example.jwttokentest2.controller;

import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request)  {
        Map<String, Object> result = tokenUtil.loginCheckToken(user, response, request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 로그아웃 요청 처리
     * @return
     */
    @GetMapping("logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> result = tokenUtil.logout(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Security Test(권한 : ROLE_ADMIN)
     * @return
     */
    @GetMapping("admin")
    public ResponseEntity<Map<String, Object>> admin() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Security Test(권한 : ROLE_ADMIN , ROLE_USER)
     * @return
     */
    @GetMapping("test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 토큰 재발급(Access-Token)
     * @param request
     * @param response
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/reissue/access")
    public ResponseEntity<Map<String, Object>> reIssueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = tokenUtil.reIssueAccessToken(request, response);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 토큰 재발급(Refresh-Token)
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/reissue/refresh")
    public ResponseEntity<Map<String, Object>> reIssueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String ,Object> result = tokenUtil.reIssueRefreshToken(request, response);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

