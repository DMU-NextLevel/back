package NextLevel.demo.message;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.util.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class KakaoMessageController {

    private final KakaoMessageService kakaoMessageService;
    private final UserRepository userRepository;

    @GetMapping("/admin/kakao")
    public ResponseEntity<?> setKakaoToken(@RequestParam("code") String code) {
        kakaoMessageService.saveRefreshToken(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/kakao")
    public ResponseEntity<?> sendKakaoMessageTest() {
        UserEntity user = userRepository.findById(JWTUtil.getUserIdFromSecurityContext()).orElseThrow(
            () -> new CustomException(ErrorCode.ACCESS_TOKEN_ERROR)
        );
        kakaoMessageService.sendMessage(user, KakaoMessageService.HELLO);

        return ResponseEntity.ok().build();
    }
}
