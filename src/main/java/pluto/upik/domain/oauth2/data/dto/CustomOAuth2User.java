package pluto.upik.domain.oauth2.data.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // SimpleGrantedAuthority import 추가
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    @Override
    public Map<String, Object> getAttributes() {
        // oAuth2Response가 가지고 있는 사용자 속성을 그대로 반환합니다.
        return oAuth2Response.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✨ 가장 중요한 수정 부분 ✨
        // 역할(role) 문자열을 이용해 표준 GrantedAuthority 구현체인 SimpleGrantedAuthority를 생성합니다.
        // Collections.singleton()을 사용해 하나의 요소만 담은 컬렉션을 더 간단하게 만듭니다.
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getName() {
        // OAuth2 공급자가 제공하는 사용자의 고유 ID를 반환하는 것이 더 좋습니다.
        // 예를 들어 Google의 'sub' 값입니다. getName()은 사용자 이름이 아닐 수 있습니다.
        // oAuth2Response에 고유 ID를 가져오는 메소드가 있다면 그것을 사용하는 것을 권장합니다.
        // 예: return oAuth2Response.getProviderId();
        // 지금은 일단 그대로 둡니다.
        return oAuth2Response.getName();
    }

    public String getEmail() {
        return oAuth2Response.getEmail();
    }
}
