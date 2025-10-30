package NextLevel.demo.project.project.service;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

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
