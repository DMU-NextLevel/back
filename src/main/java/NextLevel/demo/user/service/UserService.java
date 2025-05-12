package NextLevel.demo.user.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final ImgService imgService;
    private final LoginService loginService;

    public UserEntity getUserInfo(Long userId) {
        return userRepository.findUserFullInfoByUserId(userId);
    }

    @Transactional
    public UserEntity updateUserInfo(RequestUserCreateDto dto) {
        UserEntity oldUser = getUserInfo(dto.getId());
        UserDetailEntity oldUserDetail = oldUser.getUserDetail();

        // email 변경 불가
        if(dto.getEmail() != null && !dto.getEmail().equals(oldUserDetail.getEmail())) {
            throw new CustomException(ErrorCode.CAN_NOT_CHANGE_EMAIL);
        }

        // nick name 값 확인
        loginService.checkNickNameIsNotExist(dto.getNickName());

        dto.validateNumberFormat();

        // user Entity 정보가 모두 들어왔는지도 학인
        if(dto.validateAllData())
            oldUserDetail.setRole(UserRole.USER.name());

        ImgEntity updatedImg = imgService.updateImg(dto.getImg(), oldUser.getImg());
        dto.setImgEntity(updatedImg);

        userDetailRepository.save(oldUserDetail);
        return userRepository.save(dto.toUserEntity());
    }

}
