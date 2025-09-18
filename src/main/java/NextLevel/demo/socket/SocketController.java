package NextLevel.demo.socket;

import NextLevel.demo.util.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;

    @GetMapping
    public ResponseEntity<SseEmitter> getSocket() {
        Long userId = JWTUtil.getUserIdFromSecurityContextCanNULL();
        return ResponseEntity.ok().body(socketService.getSocket(userId));
    }

    @GetMapping("/test/{id}")
    public ResponseEntity testSocket(@PathVariable("id") Long id) {
        socketService.sendSocket(id, SocketType.NOTIFICATION, new SocketDataDto("새로운 프로젝트가 생성되었어요", LocalDateTime.now()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/all")
    public ResponseEntity testSocketAll() {
        socketService.sendSocketAll(SocketType.NOTIFICATION, new SocketDataDto("새로운 프로젝트가 생성되었어요", LocalDateTime.now()));
        return ResponseEntity.ok().build();
    }

}
