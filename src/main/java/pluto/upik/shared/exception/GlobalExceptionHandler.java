package pluto.upik.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        log.error("리소스를 찾을 수 없음: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
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
        log.error("비즈니스 예외 발생: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 유효성 검사 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(Exception ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        log.error("유효성 검사 실패: {}", errors);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "입력값 유효성 검사에 실패했습니다.",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 제약 조건 위반 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (error1, error2) -> error1
                ));

        log.error("제약 조건 위반: {}", errors);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "제약 조건 위반이 발생했습니다.",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String message = String.format("파라미터 '%s'의 값 '%s'이(가) '%s' 타입으로 변환될 수 없습니다.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        log.error("인자 타입 불일치: {}", message);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "TYPE_MISMATCH",
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 접근 거부 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.error("접근 거부: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "이 리소스에 접근할 권한이 없습니다.",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 데이터 접근 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex, WebRequest request) {
        log.error("데이터 접근 오류: ", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "DATA_ACCESS_ERROR",
                "데이터 처리 중 오류가 발생했습니다.",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다.",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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

    /**
     * 유효성 검사 오류 응답 데이터 구조
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> errors;

        public ValidationErrorResponse(int status, String code, String message, String path,
                                      LocalDateTime timestamp, Map<String, String> errors) {
            super(status, code, message, path, timestamp);
            this.errors = errors;
}

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
