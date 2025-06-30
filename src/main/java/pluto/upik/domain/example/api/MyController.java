package pluto.upik.domain.example.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pluto.upik.domain.example.data.dto.CustomOAuth2User;
import pluto.upik.domain.example.data.dto.GoogleResponse;
import pluto.upik.domain.example.repository.UserRepository;
import pluto.upik.shared.entity.User;

@Controller
@RequiredArgsConstructor
public class MyController {

    private final UserRepository userRepository;

    @GetMapping("/my")
    public String myPage(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, Model model) {

        System.out.println(customOAuth2User.getEmail());
        User user = userRepository.findByEmail(customOAuth2User.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        System.out.println(user.getRole());

        model.addAttribute("user", user);

        return "my";
    }
}

