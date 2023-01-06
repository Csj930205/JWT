package com.example.jwttokentest2.util;

import com.example.jwttokentest2.entity.Token;
import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.repository.RedisRepository;
import com.example.jwttokentest2.security.jwt.JwtProvider;
import com.example.jwttokentest2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenUtil {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisRepository redisRepository;


    /**
     * 로그인 검사
     * @param user
     * @param response
     * @return
     */
    public Map<String, Object> loginCheckToken(User user, HttpServletResponse response, HttpServletRequest request) {
        User userDetails = (User) userService.loadUserByUsername(user.getUserId());
        Map<String, Object> result = new HashMap<>();

        if ( !(passwordEncoder.matches(user.getUserPw(), userDetails.getPassword())) ) {
            result.put("result", "fail");
            result.put("code", HttpStatus.BAD_REQUEST.value());
            result.put("message", "비밀번호가 일치 하지 않습니다.");

            return result;
        }

        Token refreshTokenCheck = redisRepository.findByKey(user.getUserId());
        if (refreshTokenCheck == null) {
            Token refreshToken = jwtProvider.refreshTokenCreate(userDetails);
            redisRepository.save(refreshToken);
        }

        String token = jwtProvider.resolveAccessToken(request);
        if (token == null) {
            Token accessToken = jwtProvider.accessTokenCreate(userDetails);
            jwtProvider.setHeaderAccessToken(response, accessToken.getValue());
        }

        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());

        return result;
    }

    /**
     * 토큰 검사
     * @param request
     * @return
     */
    public Map<String, Object> getToken(HttpServletRequest request) {
        String token = jwtProvider.resolveAccessToken(request);
        Map<String, Object> result = new HashMap<>();

        if (token != null && jwtProvider.validationToken(token)) {
            result.put("result", "success");
            result.put("code", HttpStatus.OK.value());
        } else {
            result.put("result", "fail");
            result.put("code", HttpStatus.BAD_REQUEST.value());
            result.put("message", "토큰이 만료되었거나 존재하지 않습니다");
        }
        return result;
    }

    /**
     * 로그아웃(구현중)
     * @param request
     * @return
     */
    public Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String accessToken = jwtProvider.resolveAccessToken(request);

        if (accessToken == null || !(jwtProvider.validationToken(accessToken))) {
            result.put("result", "fail");
            result.put("code", HttpStatus.BAD_REQUEST.value());
            result.put("message", "토큰이 없거나 만료되었습니다.");

            return result;
        }

        result.put("result", "success");
        result.put("code", HttpStatus.OK);

        return result;
    }
}
