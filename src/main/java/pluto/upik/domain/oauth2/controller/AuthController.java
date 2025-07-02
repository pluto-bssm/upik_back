package pluto.upik.domain.oauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {

        // 현재 인증 상태 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {

            // 역할에 따라 리다이렉트
            boolean isBsmStudent = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_BSM"));

            if (isBsmStudent) {
                return "redirect:/my";
            } else {
                return "redirect:/welcome";
            }
        }

        System.out.println("미인증 사용자 - 로그인 페이지 표시");
        return "login";
    }

    @GetMapping("/welcome")
    public String welcome() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("인증된 사용자: " + authentication.getName());
            System.out.println("권한: " + authentication.getAuthorities());
        }

        return "welcome";
    }

    @GetMapping("/my")
    public String my() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("인증된 사용자: " + authentication.getName());
            System.out.println("권한: " + authentication.getAuthorities());
        }

        return "my";
    }

    @GetMapping("/logout")
    public String logout() {
        System.out.println("로그아웃 요청");
        return "redirect:/login?logout=true";
    }
}