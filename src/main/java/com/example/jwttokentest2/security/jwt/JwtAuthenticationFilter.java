package com.example.jwttokentest2.security.jwt;

import com.example.jwttokentest2.exception.CustomException;
import com.example.jwttokentest2.exception.enums.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtProvider.resolveAccessToken(request);
        try {
            if (token != null && jwtProvider.validationToken(token)) {
                setAuthentication(token);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS_TOKEN);
        }

    }

    /**
     * SecurityContext 에 Authentication 저장
     * @param token
     */
    private void setAuthentication(String token) {
        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
