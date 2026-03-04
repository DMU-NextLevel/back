package NextLevel.demo.query.make;

import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.funding.repository.FreeFundingRepository;
import NextLevel.demo.funding.repository.OptionFundingRepository;
import NextLevel.demo.funding.service.CouponService;
import NextLevel.demo.img.service.ImgPath;
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
import NextLevel.demo.funding.dto.request.RequestFreeFundingDto;
import NextLevel.demo.funding.dto.request.RequestOptionFundingDto;
import NextLevel.demo.funding.service.FundingService;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.option.OptionRepository;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import java.util.Set;
import java.util.stream.LongStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class MakeTestQuery {

    private static final int userCount = 100;
    private static final int projectCount = 1000;

    private LoginService loginService;
    private ProjectService projectService;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private OptionRepository optionRepository;
    private FundingService fundingService;
    @Autowired
    private OptionFundingRepository optionFundingRepository;
    @Autowired
    private FreeFundingRepository freeFundingRepository;

    private final TransactionTemplate transactionTemplate;

    private MockedStatic<StringUtil> stringUtilStatic;
    private MockedStatic<Files> filesStatic;

    public MakeTestQuery(
            @Autowired UserRepository userRepository,
            @Autowired UserDetailRepository userDetailRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired UserValidateService userValidateService,
            @Autowired ImgServiceImpl imgService,
            @Mock EmailService emailService,
            @Autowired CouponService couponService,

            @Autowired ProjectService projectService,
            @Autowired ProjectRepository projectRepository,
            @Autowired OptionRepository optionRepository,
            @Autowired FundingService fundingService,

            @Autowired
            PlatformTransactionManager txManager
    ) {
        loginService = new LoginService(
                userRepository,
                userDetailRepository,
                emailService,
                imgService,
                passwordEncoder,
                userValidateService,
                couponService
        );
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.optionRepository = optionRepository;
        this.fundingService = fundingService;

        this.transactionTemplate = new TransactionTemplate(txManager);

        mockInit();
    }

    private void mockInit() {
        stringUtilStatic = Mockito.mockStatic(StringUtil.class);
        stringUtilStatic
                .when(() -> StringUtil.getFormattedNumber(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        stringUtilStatic
                .when(() -> StringUtil.toLocalDate(Mockito.anyString()))
                .thenReturn(LocalDate.of(2029, 10, 10));

        filesStatic = Mockito.mockStatic(Files.class);
        filesStatic
                .when(() -> Files.write(Mockito.any(), Mockito.any(byte[].class)))
                .thenReturn(Paths.get("uri"));

        filesStatic
                .when(() -> Files.delete(Mockito.any(Path.class)))
                .thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        if (stringUtilStatic != null) stringUtilStatic.close();
        if (filesStatic != null) filesStatic.close();
    }

    @Test
    @Rollback(false)
    @Order(1)
    public void makeUser100() {
        for(int i = 0; i < userCount; i++) {
            int userId = i;
            transactionTemplate.executeWithoutResult(status -> {
                UserDetailEntity detail = loginService.register(randomUserDto(userId), new ImgPath());
                UserEntity user = detail.getUser();
                user.updatePoint(100000);
                userRepository.save(user);
            });
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
    @Rollback(false)
    @Order(2)
    public void makeProject1000() {
        for(int i = 0; i < projectCount; i++) {
            int projectId = i;
            transactionTemplate.executeWithoutResult(status -> {
                CreateProjectDto dto = randomProjectDto(projectId, userCount);
                ProjectEntity newProject = projectService.save(dto, new ImgPath());

                makeOptions(newProject, List.of(100,200,300));
            });
        }
    }

    private CreateProjectDto randomProjectDto(int count, int userCount) {
        CreateProjectDto dto = new CreateProjectDto();
        dto.setUserId(Long.valueOf(count % userCount + 1));
        dto.setGoal(Long.valueOf(count % 10 + 1) * 1000);
        dto.setStartAt(null);
        dto.setExpiredAt("mocked");
        dto.setContent("content" + count);
        dto.setTitle("title" + count);
        dto.setTitleImg(Mockito.mock(MockMultipartFile.class, "title_img" + count));
        dto.setImgs(List.of(new MultipartFile[]{
                new MockMultipartFile("project"+count+"_img1"+count, "img".getBytes()),
                new MockMultipartFile("project"+count+"_img2"+count, "img".getBytes()),
                new MockMultipartFile("project"+count+"_img3"+count, "img".getBytes())
        }));
        dto.setTags(List.of(
                Long.valueOf(count % 4 + 1),
                Long.valueOf(count % 4 + 2),
                Long.valueOf(count % 4 + 3)
        ));
        return dto;
    }

    private void makeOptions(ProjectEntity projectEntity, List<Integer> priceList) {
        List<OptionEntity> optionList = priceList.stream().map(price ->
                OptionEntity.builder()
                        .project(projectEntity)
                        .price(price)
                        .description(price + " Option")
                        .build()
        ).toList();
        optionRepository.saveAll(optionList);
    }

    @Test
    @Rollback(false)
    @Order(3)
    public void makeFunding() {
        List<ProjectEntity> allProjects = projectRepository.findAll();
        for(ProjectEntity project : allProjects) {
            transactionTemplate.executeWithoutResult(status -> {
                List<Long> funderIdList = LongStream.range(project.getUser().getId(), project.getUser().getId()+20).boxed().toList();
                List<UserEntity> funderList = userRepository.findAllById(funderIdList);

                List<OptionEntity> optionList = optionRepository.findByProjectId(project.getId());

                makeFunding(project, optionList, funderList);
            });
        }
    }

    private void makeFunding(ProjectEntity projectEntity, List<OptionEntity> optionList, List<UserEntity> funderList) {
        List<FreeFundingEntity> freeFundingEntityList = funderList.stream().map(funder->
                FreeFundingEntity.builder()
                        .price(500L)
                        .project(projectEntity)
                        .user(funder)
                        .build()
        ).toList();
        freeFundingRepository.saveAll(freeFundingEntityList);

        List<OptionFundingEntity> optionFundingList = funderList.stream().map(funder ->
            OptionFundingEntity.builder()
                    .user(funder)
                    .count(1)
                    .option(optionList.get(funder.getId().intValue() % 3))
                    .build()
        ).toList();
        optionFundingRepository.saveAll(optionFundingList);
    }

}

