package NextLevel.demo.admin.project;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.project.project.service.ProjectStatusService;
import NextLevel.demo.project.project.service.ProjectValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectService projectService;
    private final ProjectDeleteService projectDeleteService;
    private final ProjectStatusService projectStatusService;
    private final ProjectValidateService projectValidateService;

    public void getProjectList() {

    }

    public void updateProjectStatus(Long projectId, ProjectStatus status) {
        projectStatusService.updateProjectStatus(projectId, status, "by admin");
    }

    public void updateProject() {

    }

    public void removeProject(Long projectId) {
        projectDeleteService.deleteProject(projectId, null);
    }

}
