package pluto.upik.shared.oauth2jwt.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.annotation.RequireRole;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;

import java.util.Map;

@Slf4j
@Aspect
@Component
public class AuthorizationAspect {

    /**
     * ★★★ @RequireAuth 어노테이션 처리 ★★★
     */
    @Around("@annotation(requireAuth)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint, RequireAuth requireAuth) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.debug("=== @RequireAuth 체크 시작 ===");

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("인증되지 않은 사용자 접근 시도: {}", joinPoint.getSignature().getName());
            return ResponseEntity.status(401).body(Map.of(
                    "error", requireAuth.message(),
                    "code", "UNAUTHORIZED",
                    "isAuthenticated", false
            ));
        }

        log.debug("인증 확인 완료");
        return joinPoint.proceed();
    }

    /**
     * ★★★ @RequireRole 어노테이션 처리 ★★★
     */
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.debug("=== @RequireRole 체크 시작: {} ===", requireRole.value());

        // 1. 인증 체크
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("인증되지 않은 사용자의 역할 체크 시도");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "로그인이 필요합니다",
                    "code", "UNAUTHORIZED",
                    "isAuthenticated", false
            ));
        }

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();
            String requiredRole = "ROLE_" + requireRole.value().toUpperCase();

            log.debug("필요한 권한: {}, 현재 권한: {}", requiredRole, customOAuth2User.getRole());

            // 2. 권한 체크
            boolean hasRequiredRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(requiredRole));

            if (!hasRequiredRole) {
                log.warn("권한 부족 - 사용자: {}, 필요권한: {}, 현재권한: {}",
                        customOAuth2User.getUsername(), requiredRole, customOAuth2User.getRole());

                return ResponseEntity.status(403).body(Map.of(
                        "error", requireRole.message(),
                        "code", "ACCESS_DENIED",
                        "requiredRole", requireRole.value(),
                        "currentRole", customOAuth2User.getRole().replace("ROLE_", ""),
                        "isAuthenticated", true
                ));
            }

            log.debug("권한 체크 통과: {}", customOAuth2User.getUsername());
            return joinPoint.proceed();

        } catch (ClassCastException e) {
            log.error("Principal casting error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "인증 정보 처리 오류",
                    "code", "INTERNAL_ERROR"
            ));
        }
    }
}