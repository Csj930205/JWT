package com.example.jwttokentest2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants
@RequiredArgsConstructor
public enum RoleEnum {
    관리자("ADMIN"),
    일반("USER"),
    소셜("SOCIAL");

    private final String value;

    public static class ROLES {
        public static final String 관리자 = "ADMIN";
        public static final String 일반 = "USER";
        public static final String 소셜 = "SOCIAL";
    }

}
