package pluto.upik.shared.oauth2jwt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private final Key key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JWTUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration-time}") long accessTokenExpirationTime,
                   @Value("${jwt.refresh-token-expiration-time}") long refreshTokenExpirationTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role) {
        long now = System.currentTimeMillis();
        long expirationTime = "access".equals(category) ? accessTokenExpirationTime : refreshTokenExpirationTime;

        return Jwts.builder()
                .claim("category", category) // 토큰 종류 (access, refresh)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}