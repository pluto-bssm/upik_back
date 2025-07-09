package pluto.upik.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 캐싱 설정 클래스
 * 애플리케이션의 캐싱 전략을 구성합니다.
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    /**
     * 캐시 매니저를 설정합니다.
     * 
     * @return 구성된 캐시 매니저
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                "guides",
                "guideDetails", 
                "reports", 
                "userReports", 
                "targetReports"
        ));
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Caffeine 캐시 빌더를 설정합니다.
     *
     * @return 구성된 Caffeine 캐시 빌더
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 매일 자정에 모든 캐시를 비웁니다.
     */
    @CacheEvict(value = {
            "guides",
            "guideDetails",
            "reports",
            "userReports",
            "targetReports"
    }, allEntries = true)
    @Scheduled(cron = "0 0 0 * * ?")
    public void evictAllCachesAtMidnight() {
        // 메서드 내용은 비워둡니다. @CacheEvict 어노테이션이 캐시 비우기를 처리합니다.
    }
}
}