package pluto.upik.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * 간단한 Access Token 생성 (역할 자동 판단)
     */
    public String generateToken(String email) {

        // BSM 학생 여부에 따라 역할 자동 결정
        String role = email.endsWith("@bssm.hs.kr") ? "BSM_STUDENT" : "GENERAL_USER";

        return generateAccessToken(email, role);
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(String email, String role) {

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("Token: " + token.substring(0, 30) + "...");
        return token;
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String email) {

        String token = Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("Token: " + token.substring(0, 30) + "...");
        return token;
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {

        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            System.out.println("이메일 추출 실패: " + e.getMessage());
            return null;
        }
    }

    /**
     * 토큰에서 역할 추출
     */
    public String getRoleFromToken(String token) {
        try {
            return getClaims(token).get("role", String.class);
        } catch (Exception e) {
            System.out.println("역할 추출 실패: " + e.getMessage());
            return null;
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {

        try {
            Claims claims = getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("토큰 검증 실패: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("예상치 못한 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 만료 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            boolean expired = expiration.before(new Date());
            System.out.println("만료일: " + expiration);
            System.out.println("현재시간: " + new Date());
            System.out.println("만료 여부: " + (expired ? "만료됨" : "유효함"));
            return expired;
        } catch (Exception e) {
            System.out.println("만료 확인 실패: " + e.getMessage());
            return true;
        }
    }

    /**
     * Claims 추출
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("토큰 파싱 실패: " + e.getMessage());
            throw e;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}