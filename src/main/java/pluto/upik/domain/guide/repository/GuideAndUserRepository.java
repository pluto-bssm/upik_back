package pluto.upik.domain.guide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.guide.data.model.GuideAndUser;
import pluto.upik.domain.guide.data.model.GuideAndUserId;

@Repository
public interface GuideAndUserRepository extends JpaRepository<GuideAndUser, GuideAndUserId> {
    // 필요 시 커스텀 메서드 추가 가능
}