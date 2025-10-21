package NextLevel.demo.project.project.service;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectStatusService {

    private final ProjectRepository projectRepository;
    private final ProjectValidateService projectValidateService;

    @Transactional
    public void updateProjectStatus(Long projectId, ProjectStatus status, String reason) {
        ProjectEntity project = projectValidateService.getProjectEntity(projectId);
        project.updateStatus(status);
        log.info("project (id{}, title{}) status update : {}", project.getId(), project.getTitle(), reason);
    }

}
