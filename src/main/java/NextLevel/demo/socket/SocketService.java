package NextLevel.demo.socket;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SocketService {

    SseEmitter getSocket(Long userId);

    void sendSocket(long userId, SocketType type, SocketDataDto data);

    void sendSocketAll(SocketType type, SocketDataDto data);

}
