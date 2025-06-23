package pluto.upik.shared.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenExpirationMs;

    public JwtProvider(KakaoJwtProperties kakaoJwtProperties) {
        this.key = Keys.hmacShaKeyFor(kakaoJwtProperties.getSecret().getBytes());
        this.accessTokenExpirationMs = kakaoJwtProperties.getAccessTokenExpirationMs();
    }

    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String extractRole(String token) {
        return (String) parseToken(token).getBody().get("role");
    }
}