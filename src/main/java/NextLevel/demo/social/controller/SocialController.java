package NextLevel.demo.social.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.social.dto.RequestSocialCreateDto;
import NextLevel.demo.social.service.SocialService;
import NextLevel.demo.util.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/api1/social")
    public ResponseEntity create(@ModelAttribute RequestSocialCreateDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        socialService.create(dto, null);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @PutMapping("/api1/social")
    public ResponseEntity update(@ModelAttribute RequestSocialCreateDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        socialService.update(dto, null);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/social/{socialId}")
    public ResponseEntity delete(@PathVariable("socialId") Long socialId) {
        socialService.delete(socialId, JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @GetMapping("/public/social/{userId}")
    public ResponseEntity list(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", socialService.list(userId)));
    }

}
