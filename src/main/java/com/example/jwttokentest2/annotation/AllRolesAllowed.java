package com.example.jwttokentest2.annotation;

import jakarta.annotation.security.RolesAllowed;

import java.lang.annotation.*;
import static com.example.jwttokentest2.enums.RoleEnum.ROLES.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@RolesAllowed({관리자, 일반, 소셜})
public @interface AllRolesAllowed {
}
