package NextLevel.demo.admin.user;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.user.dto.user.request.RequestUpdateUserInfoDto;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/list")
    public ResponseEntity getUserList(Pageable pageable) {
        return ResponseEntity.ok().body(new SuccessResponse("success", adminUserService.getUserList(pageable)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity getUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", adminUserService.getUserInfo(userId)));
    }

    public ResponseEntity stopUser() {
        return null;
    }

    @PutMapping("/update")
    public ResponseEntity updateUser(@RequestBody @Valid RequestUpdateUserInfoDto dto) {
        adminUserService.updateUser(dto);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity removeUser(@PathVariable("userId") Long userId) {
        adminUserService.removeUser(userId);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

}
