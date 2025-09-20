package NextLevel.demo.follow;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/social/follow")
    public ResponseEntity follow(@RequestBody @Valid FollowDto dto) {
        followService.follow(JWTUtil.getUserIdFromSecurityContext(), dto.getTargetId(), dto.getFollow());
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

}
