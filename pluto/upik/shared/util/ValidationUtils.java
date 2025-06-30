package pluto.upik.shared.util;

import lombok.extern.slf4j.Slf4j;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 유효성 검사 관련 유틸리티 클래스
 * 반복적인 유효성 검사 패턴을 단순화합니다.
 */
@Slf4j
public class ValidationUtils {

    /**
     * 조건이 참인지 확인하고, 거짓이면 지정된 예외를 던집니다.
     *
     * @param condition 검사할 조건
     * @param exceptionSupplier 조건이 거짓일 때 던질 예외 공급자
     * @param <E> 예외 타입
     * @throws E 지정된 예외
     */
    public static <E extends Exception> void validateCondition(boolean condition, Supplier<E> exceptionSupplier) throws E {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 리소스가 존재하는지 확인하고, 존재하지 않으면 ResourceNotFoundException을 던집니다.
     *
     * @param exists 리소스 존재 여부
     * @param resourceType 리소스 유형
     * @param resourceId 리소스 ID
     * @throws ResourceNotFoundException 리소스가 존재하지 않을 때
     */
    public static void validateResourceExists(boolean exists, String resourceType, UUID resourceId) {
        if (!exists) {
            log.warn("{} 찾기 실패 - {}가 존재하지 않음 (id: {})", resourceType, resourceType, resourceId);
            throw new ResourceNotFoundException("해당 " + resourceType + "가 존재하지 않습니다: " + resourceId);
        }
    }

    /**
     * 비즈니스 규칙이 충족되는지 확인하고, 충족되지 않으면 BusinessException을 던집니다.
     *
     * @param condition 검사할 조건
     * @param message 예외 메시지
     * @throws BusinessException 조건이 충족되지 않을 때
     */
    public static void validateBusinessRule(boolean condition, String message) {
        if (!condition) {
            log.warn("비즈니스 규칙 위반: {}", message);
            throw new BusinessException(message);
        }
    }
}