package NextLevel.demo.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SocketUserInfo {
    private Long userId;
    private SseEmitter socket;
}
