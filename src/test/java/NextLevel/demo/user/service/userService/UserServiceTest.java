package NextLevel.demo.user.service.userService;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.user.dto.user.request.RequestUpdateUserInfoDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.util.jwt.JWTUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailRepository userDetailRepository;
    @Mock
    private UserValidateService userValidateService;
    @Mock
    private ImgService imgService;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTUtil jwtUtil;

    private UserEntity mockUser;
    private ImgEntity mockImg;

    @BeforeEach
    public void setUp() {
        mockUser = UserEntity
                .builder()
                .id(1L)
                .name("name")
                .nickName("nickname")
                .address("address")
                .point(100)
                .areaNumber("02-1111-1111")
                .number("010-1111-1111")
                .build();
        mockImg = ImgEntity.builder().id(1L).build();

        Mockito.lenient().when(userValidateService.getUserInfoWithAccessToken(Mockito.anyLong())).thenReturn(mockUser);
        Mockito.lenient().when(imgService.saveImg(Mockito.any(), Mockito.any())).thenReturn(mockImg);
        Mockito.lenient().when(imgService.updateImg(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(mockImg);
    }

    @Test
    public void updateUserEmailOrNicknameTest() {
        RequestUpdateUserInfoDto updateEmailDto = new RequestUpdateUserInfoDto();
        updateEmailDto.setId(1L); updateEmailDto.setName("email"); updateEmailDto.setValue("updated eamil");
        RequestUpdateUserInfoDto updateNicknameDto = new RequestUpdateUserInfoDto();
        updateNicknameDto.setId(1L); updateNicknameDto.setName("nickName"); updateNicknameDto.setValue("updated nickname");

        Assertions.assertAll(
            ()->Assertions.assertThrows(
                CustomException.class,
                ()->userService.updateUserInfo(updateEmailDto, null, null)
            ),
            ()->Assertions.assertThrows(
                CustomException.class,
                ()->userService.updateUserInfo(updateNicknameDto, null, null)
            )
        );
    }

    @Test
    public void updateInvalidNumberTest() {
        RequestUpdateUserInfoDto updateNumberDto = new RequestUpdateUserInfoDto();
        updateNumberDto.setId(1L); updateNumberDto.setName("number"); updateNumberDto.setValue("111-11-11");

        RequestUpdateUserInfoDto updateAreaNumberDto = new RequestUpdateUserInfoDto();
        updateAreaNumberDto.setId(1L); updateAreaNumberDto.setName("areaNumber"); updateAreaNumberDto.setValue("00-00-00");

        Assertions.assertAll(
                ()->Assertions.assertThrows(
                        CustomException.class,
                        ()->userService.updateUserInfo(updateNumberDto, null, null)
                ),
                ()->Assertions.assertThrows(
                        CustomException.class,
                        ()->userService.updateUserInfo(updateAreaNumberDto, null, null)
                )
        );
    }

    @Test
    public void updateAlreadyExistNickName() {
        Mockito.when(userValidateService.checkNickNameIsNotExist(Mockito.anyString())).thenReturn(false);
        RequestUpdateUserInfoDto updateNameDto = new RequestUpdateUserInfoDto();
        updateNameDto.setId(1L); updateNameDto.setName("nickName"); updateNameDto.setValue("updated nickname");

        Assertions.assertThrows(
                CustomException.class,
                ()->userService.updateUserInfo(updateNameDto, null, null)
        );
    }

    @Test
    public void updateUserInfoTest() {
        Mockito.when(userValidateService.checkNickNameIsNotExist(Mockito.anyString())).thenReturn(true);
        RequestUpdateUserInfoDto updateNameDto = new RequestUpdateUserInfoDto();
        updateNameDto.setId(1L); updateNameDto.setName("nickName"); updateNameDto.setValue("updated nickname");

        UserEntity updatedUser = userService.updateUserInfo(updateNameDto, null, null);

        Assertions.assertEquals("updated nickname", updatedUser.getNickName());
    }

    @Test
    public void updatePointTest() {
        RequestUpdateUserInfoDto updatePointDto = new RequestUpdateUserInfoDto();
        updatePointDto.setId(1L); updatePointDto.setName("point"); updatePointDto.setValue("5");

        CustomException notFoundMethod = Assertions.assertThrows(
                CustomException.class,
                ()->userService.updateUserInfo(updatePointDto, null, null),
                "userService :: updateUserInfo :: refraction :: getSetterMethod('', String.class) 해당 부분 수정됨?"
        );
        Assertions.assertEquals(notFoundMethod.errorCode, ErrorCode.CAN_NOT_INVOKE);
    }

    @Test
    public void updateUserImgWithNull() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));

        Assertions.assertThrows(
                CustomException.class,
                ()->userService.updateUserImg(1L, null, new ImgPath())
        );
    }

    @Test
    // imgService가 mock이기 때문에 imgPath 관련 test는 하지 않음 -> imgService에서 test 예정
    public void updateUserImgSuccessWhenHaveOldImg() {
        ImgEntity oldImg = ImgEntity.builder().id(2L).build();
        mockUser.setImg(oldImg);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        ArgumentCaptor<ArrayList<Path>> captor = ArgumentCaptor.forClass(ArrayList.class);

        UserEntity user = userService.updateUserImg(0L, Mockito.mock(MultipartFile.class) , new ImgPath());

        Assertions.assertEquals(oldImg, user.getImg());
    }

    @Test
    public void updateUserImgSuccessWhenNoOldImg() {
        mockUser.setImg(null);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));

        UserEntity user = userService.updateUserImg(0L, Mockito.mock(MultipartFile.class), new ImgPath());

        Assertions.assertEquals(mockImg, user.getImg());
    }

}
