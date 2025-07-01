package pluto.upik.domain.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pluto.upik.shared.security.annotation.BSM;

@BSM
@Controller
@ResponseBody
public class BsmController {

    @GetMapping("/bsm")
    public String bsm() {
        return "bsm";
    }
}
