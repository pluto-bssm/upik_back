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

    // ★★★ 사용자 소프트 딜리트 (계정 탈퇴) - 보안 강화 ★★★
    @Transactional
    public boolean softDeleteUser(String username) {
        try {
            // ★★★ 삭제 여부 관계없이 조회 (이미 삭제된 계정도 확인) ★★★
            Optional<User> userOpt = userRepository.findByUsernameIncludingDeleted(username);

            if (userOpt.isEmpty()) {
                log.warn("User not found for soft delete: {}", username);
                return false;
            }

            User user = userOpt.get();

            // ★★★ 이미 삭제된 사용자인지 확인 ★★★
            if ("ROLE_DELETED".equals(user.getRole())) {
                log.warn("User already deleted: {}", username);
                return false;
            }

            // ★★★ 삭제 전 로깅 (감사 목적) ★★★
            log.info("Starting soft delete for user: {} (role: {}, email: {})",
                    username, user.getRole(), user.getEmail());

            // ★★★ 요구사항에 따라 사용자 정보 변경 ★★★
            user.setRole("ROLE_DELETED");
            user.setName("deleted account");
            user.setEmail(null);
            user.setCreatedAt(null);
            user.setDollar(0);
            user.setWon(0);
            user.setStreakCount(0);
            user.setRecentDate(null);

            // ★★★ 해당 사용자의 모든 Refresh Token 삭제 ★★★
            refreshTokenRepository.findByUser(user).ifPresent(token -> {
                log.debug("Deleting refresh token for deleted user: {}", user.getId());
                refreshTokenRepository.delete(token);
            });

            userRepository.save(user);

            log.info("User soft deleted successfully: {} -> deleted account (ID: {})",
                    username, user.getId());
            return true;

        } catch (Exception e) {
            log.error("Failed to soft delete user: {}", e.getMessage(), e);
            return false;
        }
    }

    // ★★★ 삭제된 사용자인지 확인 ★★★
    public boolean isDeletedUser(String username) {
        try {
            Optional<User> userOpt = userRepository.findByUsernameIncludingDeleted(username);
            return userOpt.isPresent() && "ROLE_DELETED".equals(userOpt.get().getRole());
        } catch (Exception e) {
            log.warn("Failed to check if user is deleted: {}", e.getMessage());
            return false;
        }
    }

    // ★★★ 사용자 복구 (관리자용) ★★★
    @Transactional
    public boolean restoreUser(String userId, String newUsername, String newEmail) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            if (!"ROLE_DELETED".equals(user.getRole())) {
                log.warn("Attempted to restore non-deleted user: {}", userId);
                return false;
            }

            // 복구 로직 (필요시 구현)
            log.info("User restore requested for ID: {}", userId);
            return true;

        } catch (Exception e) {
            log.error("Failed to restore user: {}", e.getMessage(), e);
            return false;
        }
    }
}