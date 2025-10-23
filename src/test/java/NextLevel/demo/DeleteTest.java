package NextLevel.demo;

import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.user.service.UserDeleteService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("query-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@Disabled
public class DeleteTest {

    @Autowired
    private UserDeleteService userDeleteService;
    @Autowired
    private ProjectDeleteService projectDeleteService;

    @Test
    public void deleteProject() {
        projectDeleteService.deleteProject(14L, 1L, null);
    }

    @Test
    public void deleteUser() {
        userDeleteService.deleteUser(1L, null);
    }

}
