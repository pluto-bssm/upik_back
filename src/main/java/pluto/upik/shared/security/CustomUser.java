package pluto.upik.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomUser implements OAuth2User {

    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUser(Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("id"));
    }

    public String getNickname(){
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map<?, ?> kakaoAccount) {
            Object profileObj = kakaoAccount.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                Object nicknameObj = profile.get("nickname");
                return nicknameObj != null ? nicknameObj.toString() : null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomUser{" +
                "id=" + getName() +
                ", nickname='" + getNickname() + '\'' +
                ", authorities=" + getAuthorities() +
                '}';
    }
}
