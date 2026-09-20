package NextLevel.demo.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SecurityConfig uri 별 인가 결과 / ErrorCode 응답 검증
 * - 비로그인 거부 → entryPoint → NO_AUTHENTICATED
 * - 로그인 거부 → accessDeniedHandler → ErrorCodeAuthorizationResult의 ErrorCode
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityConfigTest {

    // TestController에 /admin/test가 없어서 테스트 전용으로 추가 (중첩 @RestController는 @TestConfiguration이 자동 등록)
    @TestConfiguration
    static class AdminTestControllerConfig {
        @RestController
        static class AdminTestController {
            @GetMapping("/admin/test")
            public ResponseEntity<String> adminTest() {
                return ResponseEntity.ok("adminTest");
            }
        }
    }

    // MockMvc 기본 remoteAddr — AccessTokenFilter의 ip 검증과 일치해야 함
    private static final String IP = "127.0.0.1";

    @Autowired private MockMvc mockMvc;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private UserRepository userRepository;

    private final List<UserEntity> users = new ArrayList<>();

    private Cookie userToken;
    private Cookie adminToken;
    private Cookie socialToken;

    @BeforeAll
    void setUp() {
        userToken = accessToken(saveUser(UserRole.USER));
        adminToken = accessToken(saveUser(UserRole.ADMIN));
        socialToken = accessToken(saveUser(UserRole.SOCIAL));
    }

    @AfterAll
    void tearDown() {
        userRepository.deleteAll(users);
    }

    private UserEntity saveUser(UserRole role) {
        UserEntity user = UserEntity.builder().name("security-test-" + role.name()).build();
        user.setRole(role.name());
        user = userRepository.save(user);
        users.add(user);
        return user;
    }

    private Cookie accessToken(UserEntity user) {
        String token = jwtUtil.makeToken(
            user.getId().toString(),
            Map.of("ip", IP, "role", user.getRole()),
            jwtUtil.ACCESS_TOKEN_TIME
        );
        return new Cookie(JWTUtil.ACCESS_TOKEN, token);
    }

    private static void expectError(ResultActions result, ErrorCode errorCode) throws Exception {
        result
            .andExpect(status().is(errorCode.statusCode.value()))
            .andExpect(jsonPath("$.code").value(errorCode.CustomErrorCode));
    }

    // ---- 비로그인 ----

    @Test
    void 비로그인_public_허용() throws Exception {
        mockMvc.perform(get("/public/test"))
            .andExpect(status().isOk());
    }

    @Test
    void 비로그인_api1_NO_AUTHENTICATED() throws Exception {
        expectError(mockMvc.perform(get("/api1/test")), ErrorCode.NO_AUTHENTICATED);
    }

    @Test
    void 비로그인_admin_NO_AUTHENTICATED() throws Exception {
        expectError(mockMvc.perform(get("/admin/test")), ErrorCode.NO_AUTHENTICATED);
    }

    @Test
    void 비로그인_social_NO_AUTHENTICATED() throws Exception {
        expectError(mockMvc.perform(get("/social/test")), ErrorCode.NO_AUTHENTICATED);
    }

    @Test
    void 비로그인_미등록uri_NO_AUTHENTICATED() throws Exception {
        expectError(mockMvc.perform(get("/etc/test")), ErrorCode.NO_AUTHENTICATED);
    }

    // ---- /api1 ----

    @Test
    void USER_api1_허용() throws Exception {
        mockMvc.perform(get("/api1/test").cookie(userToken))
            .andExpect(status().isOk());
    }

    @Test
    void ADMIN_api1_허용() throws Exception {
        mockMvc.perform(get("/api1/test").cookie(adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void SOCIAL_api1_NEED_ADDITIONAL_DATA() throws Exception {
        expectError(mockMvc.perform(get("/api1/test").cookie(socialToken)), ErrorCode.NEED_ADDITIONAL_DATA);
    }

    // ---- /admin ----

    @Test
    void ADMIN_admin_허용() throws Exception {
        mockMvc.perform(get("/admin/test").cookie(adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void USER_admin_NOT_ADMIN() throws Exception {
        expectError(mockMvc.perform(get("/admin/test").cookie(userToken)), ErrorCode.NOT_ADMIN);
    }

    @Test
    void SOCIAL_admin_NOT_ADMIN() throws Exception {
        expectError(mockMvc.perform(get("/admin/test").cookie(socialToken)), ErrorCode.NOT_ADMIN);
    }

    // ---- /social ----

    @Test
    void SOCIAL_social_허용() throws Exception {
        mockMvc.perform(get("/social/test").cookie(socialToken))
            .andExpect(status().isOk());
    }

    @Test
    void USER_social_허용() throws Exception {
        mockMvc.perform(get("/social/test").cookie(userToken))
            .andExpect(status().isOk());
    }

    // ---- 미등록 uri ----

    @Test
    void USER_미등록uri_SIBAL_WHAT_IS_IT() throws Exception {
        expectError(mockMvc.perform(get("/etc/test").cookie(userToken)), ErrorCode.SIBAL_WHAT_IS_IT);
    }
}
