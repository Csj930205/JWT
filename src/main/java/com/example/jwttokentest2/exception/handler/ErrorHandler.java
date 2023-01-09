package com.example.jwttokentest2.exception.handler;

import com.example.jwttokentest2.exception.CustomException;
import com.example.jwttokentest2.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> accessTokenException( CustomException cx) {
        return new ResponseEntity<>(new ErrorResponse(cx.getErrorCode()), HttpStatus.OK);
    }
}
