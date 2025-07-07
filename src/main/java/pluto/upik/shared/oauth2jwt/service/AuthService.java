package pluto.upik.shared.oauth2jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 트랜잭션 관리를 서비스 계층으로 이동
    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        // Repository의 deleteByToken 메소드는 @Modifying이 붙어있어야 함
        refreshTokenRepository.deleteByToken(token);
    }
}