package pluto.upik.shared.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 성능 모니터링을 위한 AOP 측면
 * 서비스 및 리포지토리 메서드의 실행 시간을 측정합니다.
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    /**
     * 서비스 계층 메서드의 실행 시간을 측정합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* pluto.upik.domain.*.service.*.*(..))")
    public Object measureServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureExecutionTime(joinPoint, "Service");
    }

    /**
     * 리포지토리 계층 메서드의 실행 시간을 측정합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* pluto.upik.domain.*.repository.*.*(..))")
    public Object measureRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureExecutionTime(joinPoint, "Repository");
    }

    /**
     * 리졸버 계층 메서드의 실행 시간을 측정합니다.
     *
     * @param joinPoint 조인 포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* pluto.upik.domain.*.resolver.*.*(..))")
    public Object measureResolverExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureExecutionTime(joinPoint, "Resolver");
    }

    /**
     * 메서드 실행 시간을 측정합니다.
     *
     * @param joinPoint 조인 포인트
     * @param layerName 계층 이름
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    private Object measureExecutionTime(ProceedingJoinPoint joinPoint, String layerName) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            // 실행 시간이 100ms를 초과하는 경우에만 경고 로그 출력
            if (executionTime > 100) {
                log.warn("[성능 측정] {}.{}.{} - 실행 시간: {}ms", layerName, className, methodName, executionTime);
            } else {
                log.debug("[성능 측정] {}.{}.{} - 실행 시간: {}ms", layerName, className, methodName, executionTime);
            }
        }
    }
}