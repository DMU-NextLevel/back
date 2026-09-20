package NextLevel.demo.config;

import NextLevel.demo.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.authorization.AuthorizationResult;

public class ErrorCodeAuthorizationResult implements AuthorizationResult {

    private final ErrorCode errorCode;

    public ErrorCodeAuthorizationResult(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public boolean isGranted() {
        return false;
    }
}
