package NextLevel.demo.admin.user;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.dto.user.request.RequestUpdateUserInfoDto;
import NextLevel.demo.user.dto.user.response.ResponseUserInfoDetailDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserDeleteService;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserValidateService userValidateService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDeleteService userDeleteService;

    public List<ResponseUserInfoDetailDto> getUserList(Pageable pageable) {
        return userRepository.findAll(pageable).stream().map(ResponseUserInfoDetailDto::of).toList();
    }

    public ResponseUserInfoDetailDto getUserInfo(Long userId) {
        UserEntity user = userValidateService.findUserWithUserId(userId);
        return ResponseUserInfoDetailDto.of(user);
    }

    public void stopUser() {
        // not yet
    }

    @Transactional
    public void updateUser(RequestUpdateUserInfoDto dto) {
        UserEntity oldUser = userValidateService.findUserWithUserId(dto.getId());

        if(dto.getName().equals("point")) {
            oldUser.updatePoint(Integer.parseInt(dto.getValue()));
            return;
        }

        if(dto.getName().equals("nickName") && !userValidateService.checkNickNameIsNotExist(dto.getValue()))
            throw new CustomException(ErrorCode.ALREADY_EXISTS_NICKNAME);

        oldUser.updateUserInfo(dto.getName(), dto.getValue());
    }

    public void removeUser(Long targetId) {
        userDeleteService.deleteUser(targetId, null);
    }

}
