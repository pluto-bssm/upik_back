package pluto.upik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Upik 애플리케이션 메인 클래스
 * 애플리케이션의 진입점입니다.
 */
@SpringBootApplication
@EnableScheduling
public class UpikApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpikApplication.class, args);
    }

}
