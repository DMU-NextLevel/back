package NextLevel.demo.socket;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;

public class SocketSendFailException extends CustomException {
    public SocketSendFailException() {
        super(ErrorCode.SOCKET_SEND_FAILED);
    }
}
