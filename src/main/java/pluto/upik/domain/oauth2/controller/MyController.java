package pluto.upik.domain.oauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pluto.upik.domain.oauth2.data.dto.CustomOAuth2User;
import pluto.upik.domain.oauth2.repository.UserRepository;
import pluto.upik.domain.oauth2.data.model.User;
import pluto.upik.shared.security.annotation.BSM;
import pluto.upik.shared.security.annotation.NOBSM;

@Controller
@RequiredArgsConstructor
@NOBSM
@BSM
public class MyController {

    private final UserRepository userRepository;

    @GetMapping("/my")
    public String myPage() {

        return "my";
    }
}

