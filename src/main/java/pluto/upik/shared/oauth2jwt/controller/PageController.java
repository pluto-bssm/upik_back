package pluto.upik.shared.oauth2jwt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.annotation.RequireRole;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class PageController {

    // ★★★ 메인 페이지 - 인증 필요 ★★★
    @GetMapping("/main")
    @RequireAuth(message = "메인 페이지 접근을 위해 로그인이 필요합니다")
    public ResponseEntity<?> getMainPageData() {
        return createSuccessResponse("메인 페이지", null);
    }

    // ★★★ My 페이지 - 모두 접근 가능 ★★★
    @GetMapping("/my")
    public ResponseEntity<?> getMyPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return createSuccessResponse("마이페이지입니다", null);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("isAuthenticated", false);
            response.put("message", "로그인하면 더 많은 정보를 볼 수 있습니다");
            return ResponseEntity.ok(response);
        }
    }

    // ★★★ BSM 페이지 - BSM 권한 필요 ★★★
    @GetMapping("/bsm")
    @RequireRole(value = "BSM", message = "BSM 권한이 필요합니다")
    public ResponseEntity<?> getBsmPageData() {
        return createSuccessResponse("BSM 전용 페이지", "BSM");
    }

    // ★★★ NOBSM 페이지 - NOBSM 권한 필요 ★★★
    @GetMapping("/nobsm")
    @RequireRole(value = "NOBSM", message = "NOBSM 권한이 필요합니다")
    public ResponseEntity<?> getNobsmPageData() {
        return createSuccessResponse("NOBSM 전용 페이지", "NOBSM");
    }

    // ★★★ 인증 상태 확인 API ★★★
    @GetMapping("/auth/status")
    @RequireAuth(message = "해당 페이지 접근을 위해 로그인이 필요합니다")
    public ResponseEntity<?> getAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();
                response.put("isAuthenticated", true);
                response.put("username", customOAuth2User.getUsername());
                response.put("name", customOAuth2User.getName());
                response.put("role", customOAuth2User.getRole());
                response.put("authorities", auth.getAuthorities());

                // 페이지별 접근 권한
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("main", true);
                permissions.put("my", true);
                permissions.put("bsm", hasRole(auth, "ROLE_BSM"));
                permissions.put("nobsm", hasRole(auth, "ROLE_NOBSM"));
                response.put("permissions", permissions);

            } catch (ClassCastException e) {
                log.error("Principal casting error: {}", e.getMessage());
                response.put("isAuthenticated", false);
                response.put("error", "인증 정보 처리 오류");
            }
        } else {
            response.put("isAuthenticated", false);
            Map<String, Boolean> permissions = new HashMap<>();
            permissions.put("main", false);
            permissions.put("my", true);
            permissions.put("bsm", false);
            permissions.put("nobsm", false);
            response.put("permissions", permissions);
        }

        return ResponseEntity.ok(response);
    }

    // ★★★ 공통 성공 응답 생성 ★★★
    private ResponseEntity<?> createSuccessResponse(String message, String pageType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();

        Map<String, Object> response = new HashMap<>();

        // 사용자 정보
        Optional<User> userOpt = customOAuth2User.getUser();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("username", user.getUsername());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("email", user.getEmail());
        } else {
            response.put("username", customOAuth2User.getUsername());
            response.put("name", customOAuth2User.getName());
            response.put("role", customOAuth2User.getRole());
        }

        response.put("isAuthenticated", true);
        response.put("message", message);
        response.put("authorities", auth.getAuthorities());

        if (pageType != null) {
            response.put("pageType", pageType);
        }

        log.info("{} API accessed by: {}", pageType != null ? pageType : "Main", customOAuth2User.getUsername());
        return ResponseEntity.ok(response);
    }

    // ★★★ 권한 체크 헬퍼 메서드 ★★★
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}