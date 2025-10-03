package NextLevel.demo.query.make;

import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.project.project.dto.request.CreateProjectDto;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.user.dto.RequestUserCreateDto;
import NextLevel.demo.user.repository.UserDetailRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.EmailService;
import NextLevel.demo.user.service.LoginService;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class MakeTestQuery {

    private static final int userCount = 100;
    private static final int projectCount = 1000;

    private LoginService loginService;
    private ProjectService projectService;

    public MakeTestQuery(
            @Autowired UserRepository userRepository,
            @Autowired UserDetailRepository userDetailRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired UserValidateService userValidateService,
            @Autowired ImgServiceImpl imgService,
            @Mock EmailService emailService,

            @Autowired ProjectService projectService
    ) {
        loginService = new LoginService(
                userRepository,
                userDetailRepository,
                emailService,
                imgService,
                passwordEncoder,
                userValidateService
        );
        this.projectService = projectService;
        mockInit();
    }

    private void mockInit() {
        // String Util.getFormattedNumber -> just return number
        MockedStatic<StringUtil> stringUtilStatic = Mockito.mockStatic(StringUtil.class);
        stringUtilStatic.when(() -> StringUtil.getFormattedNumber(Mockito.anyString(), Mockito.anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        MockedStatic<java.nio.file.Files> fileStatic = Mockito.mockStatic(Files.class);
        fileStatic.when(()->Files.write(Mockito.any(), Mockito.any(byte[].class))).thenReturn(Paths.get("uri"));
        fileStatic.when(()->Files.delete(Mockito.any(Path.class))).thenAnswer((Answer<Void>) invocation -> null);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void makeUser100() {
        for(int i = 0; i < userCount; i++) {
            loginService.register(randomUserDto(i), new ArrayList<>());
        }
    }

    private RequestUserCreateDto randomUserDto(int count){
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
        dto.setImg(new MockMultipartFile("user_img"+count, "img".getBytes()));
        return dto;
    }

    @Test
    @Transactional
    @Rollback(false)
    public void makeProject1000() {
        for(int i = 0; i < projectCount; i++) {
            projectService.save(randomProjectDto(i, userCount) , new ArrayList<>());
        }
    }

    private CreateProjectDto randomProjectDto(int count, int userCount) {
        CreateProjectDto dto = new CreateProjectDto();
        dto.setUserId(Long.valueOf(count % userCount + 1)); // 0이 포함되면 안됨
        dto.setGoal(Long.valueOf(count % 10 + 1) * 1000); // 1000 ~ 10000 원
        dto.setExpired("2029-10-10"); // 언젠가...
        dto.setContent("content" + count);
        dto.setTitle("title" + count);
        dto.setTitleImg(Mockito.mock(MockMultipartFile.class, "title_img" + count));
        dto.setImgs(List.of(new MultipartFile[]{
                new MockMultipartFile("project"+count+"_img1"+count, "img".getBytes()),
                new MockMultipartFile("project"+count+"_img2"+count, "img".getBytes()),
                new MockMultipartFile("project"+count+"_img3"+count, "img".getBytes())
        }));
        dto.setTags(List.of(new Long[] {
                Long.valueOf(count % 4 + 1),
                Long.valueOf(count % 4 + 2),
                Long.valueOf(count % 4 + 3)
        }));
        System.out.println(dto.toString());
        return dto;
    }

}
