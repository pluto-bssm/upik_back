package pluto.upik.shared.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 성능 모니터링을 위한 AOP 측면
 * 서비스 및 리포지토리 메서드의 실행 시간을 측정합니다.
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    @Value("${performance.threshold.warn:100}")
    private long warnThresholdMillis;

    @Value("${performance.threshold.error:500}")
    private long errorThresholdMillis;

    @Value("${performance.logging.includeParameters:false}")
    private boolean includeParameters;

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
        
        String methodParams = "";
        if (includeParameters) {
            methodParams = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", ", "(", ")"));
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();

            String message = String.format("[성능 측정] %s.%s.%s%s - 실행 시간: %dms",
                    layerName, className, methodName, methodParams, executionTime);
            if (executionTime > errorThresholdMillis) {
                log.error(message);
            } else if (executionTime > warnThresholdMillis) {
                log.warn(message);
            } else {
                log.debug(message);
            }
        }
    }
}