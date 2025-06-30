package pluto.upik.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐싱 설정 클래스
 * 애플리케이션의 캐싱 전략을 구성합니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 캐시 매니저를 설정합니다.
     * 
     * @return 구성된 캐시 매니저
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "guides", 
                "guideDetails", 
                "reports", 
                "userReports", 
                "targetReports"
        );
    }
}