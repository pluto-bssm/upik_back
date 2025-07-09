package pluto.upik.shared.oauth2jwt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String value(); // 필요한 역할 (BSM, NOBSM 등)
    String message() default "권한이 부족합니다";
}