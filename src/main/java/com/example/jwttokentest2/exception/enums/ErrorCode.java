package com.example.jwttokentest2.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 401 code
     */
    UNAUTHORIZED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access-Token 이 만료되었습니다.", "fail"),

    /**
     * 401 code
     */
    UNAUTHORIZED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh-Token 이 만료되었습니다.", "fail"),

    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String result;

}
