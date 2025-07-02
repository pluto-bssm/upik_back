package pluto.upik.domain.oauth2.data.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    @Override
    public Map<String, Object> getAttributes() {
        // null 대신 실제 속성 정보를 반환
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", oAuth2Response.getEmail());
        attributes.put("name", oAuth2Response.getName());
        attributes.put("provider", oAuth2Response.getProvider());
        attributes.put("providerId", oAuth2Response.getProviderId());
        attributes.put("role", role);

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });

        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }

    public String getUserId() {
        return oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
    }

    public String getEmail() {
        return oAuth2Response.getEmail();
    }
}