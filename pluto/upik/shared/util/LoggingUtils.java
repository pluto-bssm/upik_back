package pluto.upik.shared.util;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * 로깅 관련 유틸리티 클래스
 * 반복적인 로깅 패턴을 단순화합니다.
 */
public class LoggingUtils {

    /**
     * 메서드 실행 전후에 로깅을 수행하는 유틸리티 메서드
     *
     * @param logger 사용할 로거
     * @param methodName 메서드 이름
     * @param params 로깅할 파라미터 (키-값 쌍)
     * @param action 실행할 작업
     * @param <T> 반환 타입
     * @return 작업 결과
     */
    public static <T> T logOperation(Logger logger, String methodName, Object[] params, Supplier<T> action) {
        StringBuilder paramsStr = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) paramsStr.append(", ");
            paramsStr.append(params[i]).append(": ").append(params[i + 1]);
        }
        
        logger.info("{} 시작 - {}", methodName, paramsStr);
        
        try {
            T result = action.get();
            logger.info("{} 완료 - {}", methodName, paramsStr);
            return result;
        } catch (Exception e) {
            logger.error("{} 실패 - {}, 오류: {}", methodName, paramsStr, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 예외 발생 시 로깅하고 지정된 예외를 던지는 유틸리티 메서드
     *
     * @param logger 사용할 로거
     * @param message 로그 메시지
     * @param params 로깅할 파라미터 (키-값 쌍)
     * @param exceptionSupplier 던질 예외 공급자
     * @param <E> 예외 타입
     * @throws E 지정된 예외
     */
    public static <E extends Exception> void logAndThrow(Logger logger, String message, Object[] params, Supplier<E> exceptionSupplier) throws E {
        StringBuilder paramsStr = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) paramsStr.append(", ");
            paramsStr.append(params[i]).append(": ").append(params[i + 1]);
        }
        
        logger.warn("{} - {}", message, paramsStr);
        throw exceptionSupplier.get();
    }
}