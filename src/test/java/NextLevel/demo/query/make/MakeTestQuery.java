package NextLevel.demo.query.make;

import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.dto.login.RequestEmailLoginDto;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.EmailService;
import NextLevel.demo.user.service.LoginService;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.user.service.UserValidateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class MakeTestQuery {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailRepository userDetailRepository;
    @Autowired
    private UserValidateService userValidateService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoSpyBean
    private ImgServiceImpl imgService; // 실제 파일 저장 안함 실제 파일을 작성하는 부분은 함수화 처리해야함 그래야 test하기 편함
    @Mock
    private EmailService emailService; // 실제 작동 안함 무조건 true반환

    private LoginService loginService = new LoginService(
            userRepository,
            userDetailRepository,
            emailService,
            imgService,
            passwordEncoder,
            userValidateService
    );

    public MakeTestQuery() {
        Mockito.lenient().when(emailService.checkEmailKey(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.mockStatic(Files.class).when(()->Files.write(Mockito.any(), Mockito.any(byte[].class))).thenReturn(Paths.get("uri"));
        Mockito.mockStatic(Files.class).when(()->Files.delete(Mockito.any()));
    }

    @Test
    public void makeUser100() {
        for(int i = 0; i < 100; i++) {
            loginService.register(randomDto(i), new ArrayList<>());
        }
    }

    private RequestUserCreateDto randomDto(int count){
        RequestUserCreateDto dto = RequestUserCreateDto
                .builder()
                .name("user" + count)
                .nickName("nickname" + count)

                .email("email"+count)
                .address("address" + count)

                .number("number" + count)
                .areaNumber("areaNumber" + count)

                .password("passwrod" + count)
                .build();
        dto.setKey("key");
        dto.setImg(new MockMultipartFile("user_img"+count, new byte[]{}));
        return dto;
    }

}
