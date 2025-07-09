package pluto.upik.shared.oauth2jwt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
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

    /**
     * ★★★ 안전한 Claims 추출 (예외 처리 추가) ★★★
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            throw e; // 만료는 별도 처리를 위해 다시 throw
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Malformed JWT token", e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while parsing JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("JWT token parsing error", e);
        }
    }

    /**
     * ★★★ 안전한 Username 추출 ★★★
     */
    public String getUsername(String token) {
        try {
            return getClaims(token).get("username", String.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 username은 추출 가능
            return e.getClaims().get("username", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * ★★★ 안전한 Role 추출 ★★★
     */
    public String getRole(String token) {
        try {
            return getClaims(token).get("role", String.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 role은 추출 가능
            return e.getClaims().get("role", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * ★★★ 안전한 Category 추출 ★★★
     */
    public String getCategory(String token) {
        try {
            return getClaims(token).get("category", String.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 category는 추출 가능
            return e.getClaims().get("category", String.class);
        } catch (Exception e) {
            log.warn("Failed to extract category from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * ★★★ 안전한 만료 확인 ★★★
     */
    public Boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token is expired: {}", e.getMessage());
            return true; // 만료된 토큰
        } catch (Exception e) {
            log.warn("Failed to check token expiration: {}", e.getMessage());
            return true; // 검증 실패시 만료로 처리
        }
    }

    /**
     * ★★★ CustomSuccessHandler용 createJwt 메서드 (기존 호환성) ★★★
     */
    public String createJwt(String category, String username, String role, long expirationTime) {
        long now = System.currentTimeMillis();

        try {
            return Jwts.builder()
                    .claim("category", category)
                    .claim("username", username)
                    .claim("role", role)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + expirationTime))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create JWT token: {}", e.getMessage());
            throw new RuntimeException("JWT 토큰 생성 실패", e);
        }
    }

    /**
     * ★★★ Access Token 생성 (편의 메서드) ★★★
     */
    public String createAccessToken(String username, String role) {
        return createJwt("access", username, role,  accessTokenExpirationTime);
    }

    /**
     * ★★★ Refresh Token 생성 (편의 메서드) ★★★
     */
    public String createRefreshToken(String username, String role) {
        return createJwt("refresh", username, role,  refreshTokenExpirationTime);
    }


    /**
     * ★★★ 전체 토큰 유효성 검증 (추가) ★★★
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * ★★★ 토큰 만료 시간 확인 (추가) ★★★
     */
    public Date getExpirationDate(String token) {
        try {
            return getClaims(token).getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        } catch (Exception e) {
            log.warn("Failed to get expiration date: {}", e.getMessage());
            return null;
        }
    }
}