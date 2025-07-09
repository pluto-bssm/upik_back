package pluto.upik.shared.exception;

/**
 * 비즈니스 로직 실행 중 발생하는 예외
 * HTTP 상태 코드 400(Bad Request)에 매핑됩니다.
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 기본 생성자
     */
    public BusinessException() {
        super("비즈니스 로직 처리 중 오류가 발생했습니다.");
    }
    
    /**
     * 메시지를 지정하는 생성자
     * 
     * @param message 예외 메시지
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 메시지와 원인을 지정하는 생성자
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}