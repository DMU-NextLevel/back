package NextLevel.demo.query.projectlist;

import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SelectProjectListTest {

    @Autowired
    private SelectProjectListDslRepository selectProjectListDslRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void selectProjectListOne() {
        ResponseProjectListDto dto =selectProjectListDslRepository
                .builder(1L)
                .limit(1,0)
                .commit();

        System.out.println(dto.toString());
    }

    @Test
    public void selectProjectOne() {
        ProjectEntity project = getProject();
        System.out.println("here2");
        new ResponseProjectListDetailDto(project, 1d, 0, 0, 0L, 0);
    }

    public ProjectEntity getProject() {
        ProjectEntity project = projectRepository.findById(10L).get();
        UserEntity user = userRepository.findById(10L).get();

        System.out.println("here1");

        try {
            Field userField = ProjectEntity.class.getDeclaredField("user");
            userField.setAccessible(true);
            userField.set(project, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return project;
    }
}
