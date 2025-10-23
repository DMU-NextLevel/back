package NextLevel.demo.admin.user;

import NextLevel.demo.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public ResponseEntity getUserList(Pageable pageable) {
        return ResponseEntity.ok().body(new SuccessResponse("success", adminUserService.getUserList(pageable)));
    }

    public ResponseEntity stopUser() {
        return null;
    }

    public ResponseEntity updateUser() {
        return null;
    }

    public ResponseEntity removeUser() {
        return null;
    }

}
