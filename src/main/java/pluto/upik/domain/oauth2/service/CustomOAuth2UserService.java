package pluto.upik.domain.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pluto.upik.domain.oauth2.data.dto.CustomOAuth2User;
import pluto.upik.domain.oauth2.data.dto.GoogleResponse;
import pluto.upik.domain.oauth2.data.dto.OAuth2Response;
import pluto.upik.domain.oauth2.repository.UserRepository;
import pluto.upik.domain.oauth2.data.model.User;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public String getRole(String email){
        String domain = email.split("@")[1];
        if (domain.equals("bssm.hs.kr")) {
            return "ROLE_BSM";
        }
        return "ROLE_NOBSM";
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        String role = "";
        String email = "";
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            email = oAuth2Response.getEmail();
            role = getRole(email);
        }else {
            return null;
        }

        String name = oAuth2Response.getName();
        User existData = userRepository.findByEmail(email);
        System.out.println(existData);
        if (existData == null) {
            User user = new User(role, name, name, email);
            userRepository.save(user);
        }else{
            System.out.println("dlrkjdho");
            userRepository.updateRecentDate(email);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}

