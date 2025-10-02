package NextLevel.demo.query.projectlist;

import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class MyPage {

    @PersistenceContext
    private EntityManager em;

    RequestMyPageProjectListDto dto;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void select100() {
        List<Long> userIdList = userRepository.findAll().stream().map(UserEntity::getId).toList();
        for(int i = 0; i < 100; i++) {
            dto = randomDto(userIdList);
            LocalDateTime start = LocalDateTime.now();

            ResponseProjectListDto result = userService.mypageProjectList(dto);

            System.out.println("검색 개수 : " + result.getProjects().size() + ", 걸린 시간 + " + Duration.between(start, LocalDateTime.now()).toMillis());
            em.flush();
            em.clear();
        }
    }

    private RequestMyPageProjectListDto randomDto(List<Long> userIdList) {
        Integer random = (int) Math.random()*100; // 0~10 정수
        RequestMyPageProjectListDto dto = new RequestMyPageProjectListDto();
        dto.setUserId(userIdList.get(random % userIdList.size()));

        switch(random % 3) {
            case 0:
                dto.setType(MyPageProjectListType.PROJECT); break;
            case 1:
                dto.setType(MyPageProjectListType.FUNDING); break;
            case 2:
                dto.setType(MyPageProjectListType.LIKE); break;
            case 3:
                dto.setType(MyPageProjectListType.VIEW); break;    // view는 일단 제외 시킴
        }
        dto.setPageCount((long)random % 10);
        dto.setPage(0); // page는 무조건 첫 번째 조회하기
        return dto;
    }

}
