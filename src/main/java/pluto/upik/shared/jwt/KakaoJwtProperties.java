package pluto.upik.shared.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kakao.jwt")
public class KakaoJwtProperties {
    private String secret;
    private long accessTokenExpirationMs;
}
