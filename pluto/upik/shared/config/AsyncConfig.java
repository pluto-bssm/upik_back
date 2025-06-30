package pluto.upik.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정 클래스
 * 애플리케이션의 비동기 처리 전략을 구성합니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 비동기 작업 실행을 위한 스레드 풀 설정
     * 
     * @return 구성된 스레드 풀 실행기
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Upik-Async-");
        executor.initialize();
        return executor;
    }
}