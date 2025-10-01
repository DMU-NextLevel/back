package NextLevel.demo.user.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.user.dto.LikeDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestUpdatePasswordDto;
import NextLevel.demo.user.dto.user.request.RequestUpdateUserInfoDto;
import NextLevel.demo.user.dto.user.response.ResponseUserInfoDetailDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.user.service.LikeService;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/social/user")
public class UserController {
    private final UserService userService;
    private final LikeService likeService;
    private final JWTUtil jwtUtil;
    private final UserValidateService userValidateService;

    @GetMapping
    public ResponseEntity<?> getUserInfo() {
        Long userId = JWTUtil.getUserIdFromSecurityContext();

        UserEntity user = userValidateService.getUserInfoWithAccessToken(userId);

        ResponseUserInfoDetailDto dto = ResponseUserInfoDetailDto.of(user);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        jwtUtil.logout(response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/my-point")
    public ResponseEntity<?> getUser() {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", Map.of("point",user.getPoint())));
    }

    @PutMapping
    public ResponseEntity<?> updateUserInfo(@RequestBody @Valid RequestUpdateUserInfoDto dto, HttpServletRequest request, HttpServletResponse response) {
        Long userId = JWTUtil.getUserIdFromSecurityContext();
        dto.setId(userId);

        userService.updateUserInfo(dto, request, response);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @PutMapping("/img")
    public ResponseEntity<?> updateUserImg(@RequestParam(name = "img", required = true) MultipartFile img) {
        Long userId = JWTUtil.getUserIdFromSecurityContext();
        userService.updateUserImg(userId, img, null);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // password 변경
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid RequestUpdatePasswordDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        userService.updateUserPassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // like
    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody @Valid LikeDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        likeService.like(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

}
