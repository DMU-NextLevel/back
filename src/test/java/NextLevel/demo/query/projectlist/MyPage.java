package NextLevel.demo.query.projectlist;

import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class MyPage {

    @PersistenceContext
    EntityManager em;

    RequestMyPageProjectListDto dto;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    List<Long> userIdList;
    List<Result> resultList = new ArrayList<>();
    Long totalTime = 0L;

    @Test
    // 10/03 :: 867ms (type : project, 100개 평균)
    public void select100() {
        userIdList = userRepository.findAll().stream().map(UserEntity::getId).toList();
        selectOne();
        totalTime = 0L;
        for(int i = 0; i < 100; i++)
            selectOne();
        System.out.println("조회 종료 total time " + totalTime);
        resultList.forEach(System.out::println);
    }

    @Transactional
    public void selectOne() {
        dto = randomDto(userIdList);
        LocalDateTime start = LocalDateTime.now();

        ResponseProjectListDto result = userService.mypageProjectList(dto);
        long time =Duration.between(start, LocalDateTime.now()).toMillis();

        resultList.add(new Result(time, result.getTotalCount(), result.getProjects().size(), result));
        totalTime += time;

        em.clear();
    }

    private RequestMyPageProjectListDto randomDto(List<Long> userIdList) {
        Integer random = (int) (Math.random()*100); // 0~10 정수
        RequestMyPageProjectListDto dto = new RequestMyPageProjectListDto();
        dto.setUserId(userIdList.get(random % userIdList.size()));

        dto.setType(MyPageProjectListType.FUNDING);
//        switch(random % 3) {
//            case 0:
//                dto.setType(MyPageProjectListType.PROJECT); break;
//            case 1:
//                dto.setType(MyPageProjectListType.FUNDING); break;
//            case 2:
//                dto.setType(MyPageProjectListType.LIKE); break;
//            case 3:
//                dto.setType(MyPageProjectListType.VIEW); break;    // view는 일단 제외 시킴
//        }

        dto.setPageCount((long)random % 10 +1);
        dto.setPage(0); // page는 무조건 첫 번째 조회하기
        return dto;
    }

    class Result {
        long time;
        long totalCount;
        long limit;
        ResponseProjectListDto result;
        public Result(long time, long totalCount, long limit, ResponseProjectListDto result) {
            this.limit = limit; this.time = time; this.totalCount = totalCount; this.result = result;
        }
        public String toString() {
            return String.format("조회 결과 :: 몇개 %d, limit %d, 시간 %d ms %s", totalCount, limit, time,
                    Arrays.toString(result.getProjects().stream().map(ResponseProjectListDetailDto::getTitle).toArray()));
        }
    }
}
