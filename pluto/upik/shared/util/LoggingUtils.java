package pluto.upik.shared.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 로깅 관련 유틸리티 클래스
 * 반복적인 로깅 패턴을 단순화하고 구조화된 로깅을 지원합니다.
 */
public class LoggingUtils {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    /**
     * 요청 컨텍스트를 설정하고 MDC에 추가합니다.
     *
     * @param userId 사용자 ID (null 가능)
     * @return 생성된 요청 ID
     */
    public static String setupRequestContext(UUID userId) {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        if (userId != null) {
            MDC.put(USER_ID, userId.toString());
        }

        return requestId;
    }

    /**
     * 요청 컨텍스트를 정리합니다.
     */
    public static void clearRequestContext() {
        MDC.remove(REQUEST_ID);
        MDC.remove(USER_ID);
    }

    /**
     * 메서드 실행 전후에 로깅을 수행하는 유틸리티 메서드
     *
     * @param logger 사용할 로거
     * @param methodName 메서드 이름
     * @param params 로깅할 파라미터 맵
     * @param action 실행할 작업
     * @param <T> 반환 타입
     * @return 작업 결과
     */
    public static <T> T logOperation(Logger logger, String methodName, Map<String, Object> params, Supplier<T> action) {
        String paramsStr = formatParams(params);

        logger.info("{} 시작 - {}", methodName, paramsStr);

        long startTime = System.currentTimeMillis();
        try {
            T result = action.get();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("{} 완료 - {} (소요시간: {}ms)", methodName, paramsStr, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("{} 실패 - {} (소요시간: {}ms), 오류: {}", methodName, paramsStr, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 예외 발생 시 로깅하고 지정된 예외를 던지는 유틸리티 메서드
     *
     * @param logger 사용할 로거
     * @param message 로그 메시지
     * @param params 로깅할 파라미터 맵
     * @param exceptionSupplier 던질 예외 공급자
     * @param <E> 예외 타입
     * @throws E 지정된 예외
     */
    public static <E extends Exception> void logAndThrow(Logger logger, String message, Map<String, Object> params, Supplier<E> exceptionSupplier) throws E {
        String paramsStr = formatParams(params);

        logger.warn("{} - {}", message, paramsStr);
        throw exceptionSupplier.get();
    }

    /**
     * 파라미터 맵을 문자열로 포맷팅합니다.
     *
     * @param params 파라미터 맵
     * @return 포맷팅된 파라미터 문자열
     */
    private static String formatParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            // 민감한 정보는 마스킹 처리
            if (key.toLowerCase().contains("password") || key.toLowerCase().contains("token")) {
                sb.append(key).append(": ").append("*****");
            } else {
                sb.append(key).append(": ").append(value);
            }
        });

        return sb.toString();
    }
}
}