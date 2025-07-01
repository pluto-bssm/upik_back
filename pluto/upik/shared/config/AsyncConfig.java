package pluto.upik.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 처리 설정 클래스
 * 애플리케이션의 비동기 처리 전략을 구성합니다.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * 기본 비동기 작업 실행을 위한 스레드 풀 설정
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
        executor.setRejectedExecutionHandler(new LoggingRejectedExecutionHandler());
        executor.initialize();
        return executor;
    }

    /**
     * 알림 전송 전용 비동기 작업 실행을 위한 스레드 풀 설정
     *
     * @return 구성된 스레드 풀 실행기
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Upik-Notification-");
        executor.setRejectedExecutionHandler(new LoggingRejectedExecutionHandler());
        executor.initialize();
        return executor;
    }

    /**
     * 작업 거부 시 로깅을 수행하는 핸들러
     */
    private static class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.error("비동기 작업이 거부되었습니다. 큐 용량: {}, 활성 스레드: {}, 풀 크기: {}",
                    executor.getQueue().size(), executor.getActiveCount(), executor.getPoolSize());
            throw new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(r, executor);
        }
    }
}
}