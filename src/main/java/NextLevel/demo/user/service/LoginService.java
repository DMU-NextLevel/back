package NextLevel.demo.user.service;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.dto.login.RequestEmailLoginDto;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final EmailService emailService;
    private final ImgServiceImpl imgService;
    @Qualifier("passwordEncoder")
    private final PasswordEncoder passwordEncoder;
    private final UserValidateService userValidateService;

    @Transactional
    public UserDetailEntity socialLogin(RequestUserCreateDto socialLoginDto) {
        String socialProvider = socialLoginDto.getSocialProvider();
        String socialId = socialLoginDto.getSocialId();

        Optional<UserDetailEntity> userDetailOptional = userValidateService.findBySocialProviderAndSocialId(socialProvider, socialId);
        UserDetailEntity userDetail = null;
        if(userDetailOptional.isEmpty()) {
            // email, nick name 검증
            userValidateService.checkEmailAndNickNameElseThrow(socialLoginDto.getEmail(), socialLoginDto.getNickName());

            UserEntity user = userRepository.save(socialLoginDto.toUserEntity());

            UserDetailEntity userDetailEntity = socialLoginDto.toUserDetailEntity(user);
            userDetail = userDetailRepository.save(userDetailEntity);

            userDetail.setUser(user);
        }else{
            userDetail = userDetailOptional.get();
        }

        return userDetail;
    }

    // user UserController : post register
    @ImgTransaction
    @Transactional
    public UserDetailEntity register(@Valid RequestUserCreateDto dto, ArrayList<Path> imgPaths) {
        userValidateService.checkEmailAndNickNameElseThrow(dto.getEmail(), dto.getNickName());

        // save img get uri
        ImgEntity savedImg = null;
        try {
            savedImg = imgService.saveImg(dto.getImg(), imgPaths);
        }catch (CustomException e) {;}
        dto.setImgEntity(savedImg);

        // key 값 null 체크
        if(dto.getKey() == null || dto.getKey().isEmpty())
            throw new CustomException(ErrorCode.INPUT_REQUIRED_PARAMETER);

        emailService.checkEmailKeyAtRegister(dto.getEmail(), dto.getKey());

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        UserEntity user = userRepository.save(dto.toUserEntity());
        UserDetailEntity userDetail = userDetailRepository.save(dto.toUserDetailEntity(user));

        return userDetail;
    }

    @Transactional
    public UserDetailEntity login(RequestEmailLoginDto loginDto) {
        Optional<UserDetailEntity> user = userDetailRepository.findByEmail(loginDto.getEmail());

        if(user.isEmpty() || !passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        return user.get();
    }

}
