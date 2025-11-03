package NextLevel.demo.config;

import NextLevel.demo.exception.ErrorCode;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;

public class CustomAuthorizationDeniedException extends AuthorizationDeniedException {

    private ErrorCode errorCode;

    public CustomAuthorizationDeniedException(
            ErrorCode errorCode
    ) {
        super(errorCode.errorMessage, new AuthorizationResult() {
            @Override
            public boolean isGranted() {
                return false;
            }
        });
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
