package NextLevel.demo.admin.user;

import NextLevel.demo.user.dto.user.response.ResponseUserInfoDetailDto;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserValidateService userValidateService;
    private final UserService userService;
    private final UserRepository userRepository;

    public List<ResponseUserInfoDetailDto> getUserList(Pageable pageable) {
        return userRepository.findAll(pageable).stream().map(ResponseUserInfoDetailDto::of).toList();
    }

    public void stopUser() {
        // not yet
    }

    public void updateUser() {
        // userService.update
    }

    public void removeUser() {
        // userService. ; // delete 어디감?
    }

}
