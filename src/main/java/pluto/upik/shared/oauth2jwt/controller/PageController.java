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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/main")
    @RequireAuth(message = "메인 페이지 접근을 위해 로그인이 필요합니다")
    public ResponseEntity<?> getMainPageData() {
        return createSuccessResponse("메인 페이지", null);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPageData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return createSuccessResponse("마이페이지입니다", null);
        } else {
            return ResponseEntity.ok(Map.of(
                    "isAuthenticated", false,
                    "message", "로그인하면 더 많은 정보를 볼 수 있습니다"
            ));
        }
    }

    @GetMapping("/bsm")
    @RequireRole(value = "BSM", message = "BSM 권한이 필요합니다")
    public ResponseEntity<?> getBsmPageData() {
        return createSuccessResponse("BSM 전용 페이지", "BSM");
    }

    @GetMapping("/nobsm")
    @RequireRole(value = "NOBSM", message = "NOBSM 권한이 필요합니다")
    public ResponseEntity<?> getNobsmPageData() {
        return createSuccessResponse("NOBSM 전용 페이지", "NOBSM");
    }

    private ResponseEntity<?> createSuccessResponse(String message, String pageType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("role", user.getRole());
        response.put("isAuthenticated", true);
        response.put("message", message);

        if (pageType != null) {
            response.put("pageType", pageType);
        }

        return ResponseEntity.ok(response);
    }
}