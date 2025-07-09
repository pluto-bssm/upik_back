package pluto.upik.shared.oauth2jwt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"<http://localhost:5173>"}, allowCredentials = "true")
public class PageController {

    // ★★★ 메인 페이지 데이터 - 인증된 사용자만 ★★★
    @GetMapping("/main")
    public ResponseEntity<?> getMainPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "인증이 필요합니다",
                    "code", "UNAUTHORIZED",
                    "isAuthenticated", false
            ));
        }

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();
            Optional<User> userOpt = customOAuth2User.getUser();

            Map<String, Object> response = new HashMap<>();

            System.out.println(userOpt.isPresent());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("username", user.getUsername());
                response.put("name", user.getName());
                response.put("role", user.getRole());
                response.put("email", user.getEmail());
                response.put("dollar", user.getDollar());
                response.put("won", user.getWon());
                response.put("streakCount", user.getStreakCount());
            } else {
                response.put("username", customOAuth2User.getUsername());
                response.put("name", customOAuth2User.getName());
                response.put("role", customOAuth2User.getRole());
            }

            response.put("isAuthenticated", true);
            response.put("message", "메인 페이지에 오신 것을 환영합니다!");
            response.put("authorities", auth.getAuthorities());

            log.info("Main API accessed by: {}", customOAuth2User.getUsername());
            return ResponseEntity.ok(response);

        } catch (ClassCastException e) {
            log.error("Principal casting error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "인증 정보 처리 오류",
                    "code", "INTERNAL_ERROR",
                    "isAuthenticated", false
            ));
        }
    }

    // ★★★ My 페이지 데이터 - 모두 접근 가능 ★★★
    @GetMapping("/my")
    public ResponseEntity<?> getMyPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();
                response.put("username", customOAuth2User.getUsername());
                response.put("name", customOAuth2User.getName());
                response.put("role", customOAuth2User.getRole());
                response.put("authorities", auth.getAuthorities());
                response.put("isAuthenticated", true);
                response.put("message", "안녕하세요, " + customOAuth2User.getName() + "님!");

                // ★★★ 사용자 상세 정보 (있으면) ★★★
                Optional<User> userOpt = customOAuth2User.getUser();
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    response.put("email", user.getEmail());
                    response.put("dollar", user.getDollar());
                    response.put("won", user.getWon());
                    response.put("streakCount", user.getStreakCount());
                }

            } catch (ClassCastException e) {
                log.error("Principal casting error in /my: {}", e.getMessage());
                response.put("isAuthenticated", false);
                response.put("message", "인증 정보 오류");
                response.put("error", "CAST_ERROR");
            }
        } else {
            response.put("isAuthenticated", false);
            response.put("message", "로그인 하세요");
        }

        return ResponseEntity.ok(response);
    }

    // ★★★ BSM 페이지 데이터 - ROLE_BSM만 접근 ★★★
    @GetMapping("/bsm")
    public ResponseEntity<?> getBsmPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ★★★ 디버깅 로그 추가 ★★★
        log.info("=== BSM API 접근 시도 ===");
        log.info("Authentication: {}", auth);
        log.info("Is Authenticated: {}", auth != null ? auth.isAuthenticated() : "null");
        log.info("Principal Type: {}", auth != null && auth.getPrincipal() != null ? auth.getPrincipal().getClass().getSimpleName() : "null");
        log.info("Principal: {}", auth != null ? auth.getPrincipal() : "null");
        log.info("Authorities: {}", auth != null ? auth.getAuthorities() : "null");

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("인증되지 않은 사용자의 BSM 접근 시도");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "로그인이 필요합니다",
                    "code", "UNAUTHORIZED",
                    "isAuthenticated", false
            ));
        }

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();

            // ★★★ 더 자세한 디버깅 ★★★
            log.info("CustomOAuth2User Username: {}", customOAuth2User.getUsername());
            log.info("CustomOAuth2User Role: {}", customOAuth2User.getRole());
            log.info("CustomOAuth2User Authorities: {}", customOAuth2User.getAuthorities());

            // ★★★ 권한 체크 디버깅 ★★★
            log.info("=== 권한 체크 시작 ===");
            auth.getAuthorities().forEach(authority -> {
                log.info("Authority: {}", authority.getAuthority());
                log.info("Authority equals ROLE_BSM: {}", authority.getAuthority().equals("ROLE_BSM"));
            });

            boolean hasBsmRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_BSM"));

            log.info("Has BSM Role: {}", hasBsmRole);

            if (!hasBsmRole) {
                log.warn("권한 부족 - 현재 권한: {}, 필요 권한: ROLE_BSM", customOAuth2User.getRole());
                return ResponseEntity.status(403).body(Map.of(
                        "error", "BSM 권한이 필요합니다",
                        "code", "ACCESS_DENIED",
                        "requiredRole", "BSM",
                        "currentRole", customOAuth2User.getRole(),
                        "authorities", auth.getAuthorities().toString()
                ));
            }

            // ★★★ 성공 시 응답 - 이 부분이 반드시 필요! ★★★
            Map<String, Object> response = new HashMap<>();
            response.put("username", customOAuth2User.getUsername());
            response.put("name", customOAuth2User.getName());
            response.put("role", customOAuth2User.getRole());
            response.put("authorities", auth.getAuthorities());
            response.put("isAuthenticated", true);
            response.put("message", "BSM 전용 페이지입니다!");
            response.put("pageType", "BSM");

            log.info("BSM API 접근 성공 - User: {}", customOAuth2User.getUsername());
            return ResponseEntity.ok(response);

        } catch (ClassCastException e) {
            log.error("Principal casting error in /bsm: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "인증 정보 처리 오류",
                    "code", "INTERNAL_ERROR"
            ));
        }
    }

    // ★★★ NOBSM 페이지 데이터 - ROLE_NOBSM만 접근 ★★★
    @GetMapping("/nobsm")
    public ResponseEntity<?> getNobsmPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "로그인이 필요합니다",
                    "code", "UNAUTHORIZED",
                    "isAuthenticated", false
            ));
        }

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();

            // ★★★ 권한 체크 ★★★
            boolean hasNobsmRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_NOBSM"));

            if (!hasNobsmRole) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "NOBSM 권한이 필요합니다",
                        "code", "ACCESS_DENIED",
                        "requiredRole", "NOBSM",
                        "currentRole", customOAuth2User.getRole()
                ));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("username", customOAuth2User.getUsername());
            response.put("name", customOAuth2User.getName());
            response.put("role", customOAuth2User.getRole());
            response.put("authorities", auth.getAuthorities());
            response.put("isAuthenticated", true);
            response.put("message", "NOBSM 전용 페이지입니다!");
            response.put("pageType", "NOBSM");

            log.info("NOBSM API accessed by: {}", customOAuth2User.getUsername());
            return ResponseEntity.ok(response);

        } catch (ClassCastException e) {
            log.error("Principal casting error in /nobsm: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "인증 정보 처리 오류",
                    "code", "INTERNAL_ERROR"
            ));
        }
    }

    // ★★★ 인증 상태 확인 API ★★★
    @GetMapping("/auth/status")
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

                // ★★★ 페이지별 접근 권한 정보 ★★★
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("main", true); // 인증된 사용자는 메인 접근 가능
                permissions.put("my", true);   // 모두 접근 가능
                permissions.put("bsm", auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_BSM")));
                permissions.put("nobsm", auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_NOBSM")));

                response.put("permissions", permissions);

            } catch (ClassCastException e) {
                log.error("Principal casting error in /auth/status: {}", e.getMessage());
                response.put("isAuthenticated", false);
                response.put("error", "인증 정보 처리 오류");
            }
        } else {
            response.put("isAuthenticated", false);

            // ★★★ 비인증 사용자 권한 정보 ★★★
            Map<String, Boolean> permissions = new HashMap<>();
            permissions.put("main", false);
            permissions.put("my", true);    // My 페이지만 접근 가능
            permissions.put("bsm", false);
            permissions.put("nobsm", false);
            response.put("permissions", permissions);
        }

        return ResponseEntity.ok(response);
    }
}