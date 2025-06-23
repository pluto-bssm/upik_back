package pluto.upik.shared.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoTokenService {

    private final JwtProvider jwtProvider;

    public String generateAccessToken(String userId, String roles) {
        return jwtProvider.generateToken(userId, roles);
    }

    public String extractUserId(String token) {
        return jwtProvider.extractUserId(token);
    }
}