package com.example.jwttokentest2.util;

import com.example.jwttokentest2.entity.Token;
import com.example.jwttokentest2.entity.User;
import com.example.jwttokentest2.repository.RedisRepository;
import com.example.jwttokentest2.security.jwt.JwtProvider;
import com.example.jwttokentest2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Map<String, Object> loginCheckToken(User user, HttpServletResponse response, HttpServletRequest request) {
        User userDetails = (User) userService.loadUserByUsername(user.getUserId());
        String token = jwtProvider.resolveAccessToken(request);
        Token refreshTokenCheck = redisRepository.findByKey(user.getUserId());

        Map<String, Object> result = new HashMap<>();

        if ( !(passwordEncoder.matches(user.getUserPw(), userDetails.getPassword())) ) {
            result.put("result", "fail");
            result.put("code", HttpStatus.BAD_REQUEST.value());
            result.put("message", "비밀번호가 일치 하지 않습니다.");

            return result;
        }

        if (token == null) {
            Token accessToken = jwtProvider.accessTokenCreate(userDetails);
            jwtProvider.setHeaderAccessToken(response, accessToken.getValue());
        }

        if (refreshTokenCheck == null) {
            Token refreshToken = jwtProvider.refreshTokenCreate(userDetails);
            redisRepository.save(refreshToken);
        }

        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());

        return result;
    }

    /**
     * Token 재발급
     * @param request
     * @param response
     * @return
     */
    @Transactional
    public Map<String, Object> reIssue(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtProvider.resolveAccessToken(request);
        String userId = jwtProvider.getUserId(token);
        User user = (User) userService.loadUserByUsername(userId);
        Token refreshToken = redisRepository.findByKey(userId);

        if ( jwtProvider.validationToken(refreshToken.getValue()) ) {
            Token newAccessToken = jwtProvider.accessTokenCreate(user);
            jwtProvider.setHeaderAccessToken(response, newAccessToken.getValue());
        } else {
            Token newAccessToken = jwtProvider.accessTokenCreate(user);
            Token newRefreshToken = jwtProvider.refreshTokenCreate(user);
            jwtProvider.setHeaderAccessToken(response, newAccessToken.getValue());
            redisRepository.save(newRefreshToken);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        result.put("message", "새로운 토크이 발급되었습니다.");
        return result;
    }
}
