package NextLevel.demo.user.service;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.dto.login.RequestUserLoginDto;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    private final ImgService imgService;

    // use refresh token filter
    public UserDetailEntity findUserDetailByUserId(Long userId) {
        return userDetailRepository.findUserDetailByUserId(userId);
    }

    @Transactional
    public void socialLogin(RequestUserCreateDto socialLoginDto, HttpServletResponse response) {
        String socialProvider = socialLoginDto.getSocialProvider();
        String socialId = socialLoginDto.getSocialId();

        Optional<UserDetailEntity> userDetailOptional = userDetailRepository.findBySocialProviderAndSocialId(socialProvider, socialId);
        UserDetailEntity userDetail = null;
        if(userDetailOptional.isEmpty()) {
            // email, nick name 검증
            checkEmailAndNickNameElseThrow(socialLoginDto.getEmail(), socialLoginDto.getNickName());

            UserEntity user = userRepository.save(socialLoginDto.toUserEntity());

            UserDetailEntity userDetailEntity = socialLoginDto.toUserDetailEntity(user);
            userDetailEntity.setRole(UserRole.SOCIAL.name());
            userDetail = userDetailRepository.save(userDetailEntity);
        }else{
            userDetail = userDetailOptional.get();
        }

        jwtUtil.addRefresh(response, userDetail.getUser().getId(), userDetail.getUUID());
    }

    // user UserController : post register
    @Transactional
    public UserDetailEntity register(RequestUserCreateDto dto) {
        checkEmailAndNickNameElseThrow(dto.getEmail(), dto.getNickName());

        dto.validateNumberFormat();

        // save img get uri
        ImgEntity savedImg = null;
        try {
            savedImg = imgService.saveImg(dto.getImg());
        }catch (CustomException e) {;}
        dto.setImgEntity(savedImg);

        // key 값 null 체크
        if(dto.getKey() == null || dto.getKey().isEmpty())
            throw new CustomException(ErrorCode.INPUT_REQUIRED_PARAMETER);

        emailService.checkEmailKeyAtRegister(dto.getEmail(), dto.getKey());

        if(dto.validateAllData())
            dto.setRole(UserRole.USER.name());

        UserEntity user = userRepository.save(dto.toUserEntity());
        UserDetailEntity userDetail = userDetailRepository.save(dto.toUserDetailEntity(user));

        return userDetail;
    }

    @Transactional
    public UserDetailEntity login(RequestUserLoginDto loginDto) {
        Optional<UserDetailEntity> user = userDetailRepository.findByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());

        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        return user.get();
    }

    // check email nick name  else throw CustomException
    public void checkEmailAndNickNameElseThrow(String email, String nickName) {
        if(!checkEmailIsNotExist(email))
            throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
        if(!checkNickNameIsNotExist(nickName))
            throw new CustomException(ErrorCode.ALREADY_EXISTS_NICKNAME);
    }

    public boolean checkEmailIsNotExist(String email) {
        return userDetailRepository.findByEmail(email).isEmpty();
    }

    public boolean checkNickNameIsNotExist(String nickName) {
        return userRepository.findUserByNickName(nickName).isEmpty();
    }
}
