//package NextLevel.demo.query.make;
//
//import NextLevel.demo.user.dto.RequestUserCreateDto;
//import NextLevel.demo.user.dto.login.RequestEmailLoginDto;
//import NextLevel.demo.user.service.LoginService;
//import NextLevel.demo.user.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//
//@SpringBootTest
//@ActiveProfiles("query-test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ExtendWith(MockitoExtension.class)
//public class MakeTestQuery {
//
//    @Autowired
//    private LoginService loginService;
//
//    @Test
//    public void makeUser100() {
//        for(int i = 0; i < 100; i++) {
//            loginService.register(randomDto(), new ArrayList<>());
//        }
//    }
//
//    private RequestUserCreateDto randomDto(int count){
//        RequestUserCreateDto dto = RequestUserCreateDto
//                .builder()
//                .name("user" + count)
//                .email("email"+count)
//                .
//    }
//
//}
