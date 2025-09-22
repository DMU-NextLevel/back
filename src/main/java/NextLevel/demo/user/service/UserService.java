package NextLevel.demo.user.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestUpdatePasswordDto;
import NextLevel.demo.user.dto.user.request.RequestUpdateUserInfoDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserProjectDslRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.util.StringUtil;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserValidateService userValidateService;
    private final ImgService imgService;
    @Qualifier("passwordEncoder")
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final UserProjectDslRepository userProjectDslRepository;

    @Transactional
    public UserEntity updateUserInfo(RequestUpdateUserInfoDto dto, HttpServletRequest request, HttpServletResponse response) {
        UserEntity oldUser = userValidateService.getUserInfoWithAccessToken(dto.getId());

        switch(dto.getName()){
            case "email":
                throw new CustomException(ErrorCode.CAN_NOT_CHANGE_EMAIL);
            case "nickName":
                if(!userValidateService.checkNickNameIsNotExist(dto.getValue()))
                    throw new CustomException(ErrorCode.ALREADY_EXISTS_NICKNAME);
                break;
        }

        try {
            Method setterMethod = UserEntity.class.getDeclaredMethod(StringUtil.setGetterName(dto.getName()), String.class);
            setterMethod.invoke(oldUser, dto.getValue());
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof CustomException)
                throw (CustomException) e.getTargetException();
            else
                throw new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, e.getTargetException().getMessage());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.CAN_NOT_INVOKE, dto.getName());
        }

        oldUser.checkRole();
        userRepository.save(oldUser);

        jwtUtil.refreshAccessToken(request, response, oldUser.getRole());
        return oldUser;
    }

    @Transactional
    public void updateUserPassword(RequestUpdatePasswordDto dto) {
        UserEntity user = userRepository.findUserFullInfoByUserId(dto.getUserId()).orElseThrow(
            ()->{throw new CustomException(ErrorCode.ACCESS_TOKEN_ERROR);}
        );
        if(!passwordEncoder.matches(dto.getOldPassword(), user.getUserDetail().getPassword()))
            throw new CustomException(ErrorCode.LOGIN_FAILED);

        String newPassword = passwordEncoder.encode(dto.getNewPassword());

        log.info("passwd encode " + newPassword);

        userDetailRepository.updatePasswordByUserId(newPassword, user.getId());
    }

    @ImgTransaction
    @Transactional
    public UserEntity updateUserImg(Long userId, MultipartFile img, ImgPath imgPath) {
        UserEntity oldUser = userRepository.findById(userId).orElseThrow(
            ()->{throw new CustomException(ErrorCode.ACCESS_TOKEN_ERROR);}
        );
        if(img == null)
            throw new CustomException(ErrorCode.INPUT_REQUIRED_PARAMETER);

        if(oldUser.getImg() == null)
            oldUser.setImg(imgService.saveImg(img, imgPath));
        else
            imgService.updateImg(img, oldUser.getImg(), imgPath);
        return oldUser;
    }

    @Transactional
    public void updateTempPassword(String email) { // 임시 비밀번호 아직 미정
        UserEntity user = userRepository.findUserByEmail(email).orElseThrow(
                ()->{throw new CustomException(ErrorCode.NOT_FOUND, "email");}
        );

        String randomPassword = Base64.getEncoder().encodeToString( String.valueOf(new Random().nextDouble()).getBytes() );
        log.info("email : " + email + " new random password : " + randomPassword);

        // emailService.sendEmailCode(email, );

        user.getUserDetail().setPassword(randomPassword);
    }

    public ResponseProjectListDto mypageProjectList(@Valid RequestMyPageProjectListDto dto) {
        ResponseProjectListDto result = userProjectDslRepository.myProject(dto);

        if(dto.getType().equals(MyPageProjectListType.VIEW)){
            // sort !!
            Collections.sort(result.getProjects(), (a, b)->{
                return a.getProjectViewCreateAt().isBefore(b.getProjectViewCreateAt())? 1:-1;
            });
        }

        return result;
    }

}
