package pluto.upik.shared.oauth2jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        try {
            refreshTokenRepository.deleteByToken(token);
            log.info("Refresh token deleted successfully");
        } catch (Exception e) {
            log.warn("Failed to delete refresh token: {}", e.getMessage());
        }
    }

    @Transactional
    public boolean softDeleteUser(String username) {
        try {
            Optional<User> userOpt = userRepository.findByUsernameIncludingDeleted(username);

            if (userOpt.isEmpty() || "ROLE_DELETED".equals(userOpt.get().getRole())) {
                return false;
            }

            User user = userOpt.get();

            // 사용자 정보 삭제 처리
            user.setRole("ROLE_DELETED");
            user.setName("deleted account");
            user.setEmail(null);
            user.setDollar(0);
            user.setWon(0);
            user.setStreakCount(0);

            // Refresh Token 삭제
            refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

            userRepository.save(user);
            return true;

        } catch (Exception e) {
            log.error("Failed to soft delete user: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean isDeletedUser(String username) {
        try {
            Optional<User> userOpt = userRepository.findByUsernameIncludingDeleted(username);
            return userOpt.isPresent() && "ROLE_DELETED".equals(userOpt.get().getRole());
        } catch (Exception e) {
            return false;
        }
    }
}