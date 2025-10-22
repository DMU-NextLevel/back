package NextLevel.demo.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    public ResponseEntity getUserList() {
        return null;
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
