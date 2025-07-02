package pluto.upik.shared.oauth2jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pluto.upik.shared.oauth2jwt.entity.RefreshToken;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰 삭제 (토큰 값으로 삭제)
     */
    public void deleteRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
        } else {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }
    }

    /**
     * 리프레시 토큰 삭제 (userId로 삭제)
     */
    public void deleteRefreshTokenByUserId(String userId) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(userId);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
        } else {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
    }
}