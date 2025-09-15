package NextLevel.demo.user.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.user.auth.GoogleService;
import NextLevel.demo.user.auth.KakaoService;
import NextLevel.demo.user.auth.NaverService;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/public/auth")
public class SocialLoginController {

    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;
    private final JWTUtil jwtUtil;

    @GetMapping("/google")
    public ResponseEntity<?> googleSocial(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            return total(googleService.getUserFromGoogle(code), request, response);
        }catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoSocial(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            return total(kakaoService.login(code), request, response);
        }catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/naver")
    public ResponseEntity<?> naverSocial(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            return total(naverService.login(code), request, response);
        }catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private ResponseEntity<?> total(UserDetailEntity userDetail, HttpServletRequest request, HttpServletResponse response) {
        jwtUtil.addRefresh(response, userDetail.getUserId(), userDetail.getUUID());
        jwtUtil.addAccess(response, userDetail.getUserId(), request, userDetail.getUser().getRole());

        log.info("social login success");

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("social login success", null));
    }

}
