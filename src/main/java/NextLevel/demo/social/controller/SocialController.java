package NextLevel.demo.social.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.social.dto.RequestSocialCreateDto;
import NextLevel.demo.social.dto.SocialLikeDto;
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

    @PostMapping("/social/social")
    public ResponseEntity create(@ModelAttribute RequestSocialCreateDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        socialService.create(dto, null);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @PutMapping("/social/social")
    public ResponseEntity update(@ModelAttribute RequestSocialCreateDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        socialService.update(dto, null);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/social/social/{socialId}")
    public ResponseEntity delete(@PathVariable("socialId") Long socialId) {
        socialService.delete(socialId, JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @GetMapping("/public/social/{userId}")
    public ResponseEntity list(@PathVariable("userId") Long targetUserId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", socialService.list(targetUserId,JWTUtil.getUserIdFromSecurityContextCanNULL())));
    }

    @PostMapping("/social/social/like")
    public ResponseEntity socialLike(@RequestBody SocialLikeDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        socialService.socialLike(dto);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

}
