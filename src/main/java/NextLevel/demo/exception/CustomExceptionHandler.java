package NextLevel.demo.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        e.printStackTrace();
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.errorCode.CustomErrorCode);
        map.put("message", e.getMessage());
        return ResponseEntity.status(e.errorCode.statusCode).body(map);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", ErrorCode.INPUT_REQUIRED_PARAMETER.CustomErrorCode);
        map.put("message", ErrorCode.INPUT_REQUIRED_PARAMETER.errorMessage);
        return ResponseEntity.status(ErrorCode.INPUT_REQUIRED_PARAMETER.statusCode).body(map);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body("요청 URL에 대한 HTTP 메소드가 올바르지 않습니다");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        Map<String, Object> map = new HashMap<>();
        map.put("code", "05000");
        map.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }
}
