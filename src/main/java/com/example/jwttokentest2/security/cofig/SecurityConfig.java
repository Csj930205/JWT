package com.example.jwttokentest2.security.cofig;

import com.example.jwttokentest2.security.jwt.JwtAccessDeniedHandler;
import com.example.jwttokentest2.security.jwt.JwtAuthenticationFilter;
import com.example.jwttokentest2.security.jwt.JwtExceptionFilter;
import com.example.jwttokentest2.security.jwt.JwtLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    /**
     * 비밀번호 암호화
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증을 처리하는 인터페이스
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Security 설정
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable();

        http.csrf().disable();

        http.formLogin().disable();

        http
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                .permitAll();

        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/**").permitAll()
                );


        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(jwtExceptionFilter,
                        JwtAuthenticationFilter.class);

        http.exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler);

        return http.build();
    }
}
