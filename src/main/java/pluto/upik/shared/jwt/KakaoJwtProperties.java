package pluto.upik.shared.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "kakao.jwt")
public class KakaoJwtProperties {
    private String secret;
    private long accessTokenExpirationMs;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }
}
