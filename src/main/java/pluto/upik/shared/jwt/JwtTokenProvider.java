package pluto.upik.shared.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    private Key key;

    // 의존성 주입 후 초기화를 수행하는 메소드
    @PostConstruct
    public void init() {
        // secretKey를 Base64 디코딩하여 Key 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 생성
    public String createAccessToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(email) // 토큰의 주체 (principal)로 이메일 사용
                .claim("role", role) // ★ 가장 중요: 권한 정보를 Claim에 추가
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보(Authentication)를 추출하는 메소드
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        // 여기서 User는 Spring Security에서 제공하는 User 객체
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성을 검증하는 메소드
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명일 때
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰일 때
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰일 때
        } catch (IllegalArgumentException e) {
            // JWT 클레임 문자열이 비어있을 때
        }
        return false;
    }

    /**
     * accessToken에서 클레임(Payload) 정보를 추출하는 메소드
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도 클레임은 반환
            return e.getClaims();
        }
    }
}
