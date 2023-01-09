package com.example.jwttokentest2.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@RedisHash(value = "refreshToken")
@Getter
@Setter
@NoArgsConstructor
public class Token {
    @Id
    @Indexed
    private String key;

    private String value;

    @TimeToLive
    private Long expiredTime;

    @Builder
    public Token(String key, String value, Long expiredTime) {
        this.key = key;
        this.value = value;
        this.expiredTime = expiredTime;
    }
}
