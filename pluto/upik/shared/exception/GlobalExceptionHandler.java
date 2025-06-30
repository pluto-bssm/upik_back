package pluto.upik.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션에서 발생하는 모든 예외를 일관된 방식으로 처리합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ResourceNotFoundException 처리
     * 
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("리소스를 찾을 수 없음: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * BusinessException 처리
     * 
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest request) {
        log.warn("비즈니스 예외 발생: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_ERROR",
                ex.getMessage(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * IllegalArgumentException 처리
     * 
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        log.warn("잘못된 인자 예외 발생: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 처리되지 않은 모든 예외 처리
     * 
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("처리되지 않은 예외 발생: ", ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다.",
                request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * GraphQL 예외 처리 - ResourceNotFoundException
     * 
     * @param ex 발생한 예외
     * @return 오류 응답
     */
    @GraphQlExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleGraphQLResourceNotFound(ResourceNotFoundException ex) {
        log.warn("GraphQL 리소스를 찾을 수 없음: {}", ex.getMessage());
        
        return createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                "GraphQL 요청"
        );
    }

    /**
     * GraphQL 예외 처리 - BusinessException
     * 
     * @param ex 발생한 예외
     * @return 오류 응답
     */
    @GraphQlExceptionHandler(BusinessException.class)
    public ErrorResponse handleGraphQLBusiness(BusinessException ex) {
        log.warn("GraphQL 비즈니스 예외 발생: {}", ex.getMessage());
        
        return createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_ERROR",
                ex.getMessage(),
                "GraphQL 요청"
        );
    }

    /**
     * 오류 응답 객체를 생성합니다.
     * 
     * @param status HTTP 상태 코드
     * @param code 오류 코드
     * @param message 오류 메시지
     * @param path 요청 경로
     * @return 생성된 오류 응답
     */
    private ErrorResponse createErrorResponse(int status, String code, String message, String path) {
        return new ErrorResponse(status, code, message, path, LocalDateTime.now());
    }

    /**
     * 오류 응답 데이터 구조
     */
    public static class ErrorResponse {
        private final int status;
        private final String code;
        private final String message;
        private final String path;
        private final LocalDateTime timestamp;

        public ErrorResponse(int status, String code, String message, String path, LocalDateTime timestamp) {
            this.status = status;
            this.code = code;
            this.message = message;
            this.path = path;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}