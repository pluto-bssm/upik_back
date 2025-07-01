package pluto.upik.domain.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pluto.upik.shared.security.annotation.ADMIN;
import pluto.upik.shared.security.annotation.PUBLIC;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage() {

        return "main";
    }
}
