package NextLevel.demo.project.project.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectValidateService {
    private final ProjectRepository projectRepository;
    private final UserValidateService userValidateService;

    public ProjectEntity getProjectEntity(Long id) {
        return projectRepository.findById(id).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "project");}
        );
    }

    public ProjectEntity validateAuthor(Long projectId, Long userId) {
        ProjectEntity project = getProjectEntity(projectId);
        validateAuthor(project, userId);
        return project;
    }

    public void validateAuthor(ProjectEntity project, Long userId) {
        UserEntity user = userValidateService.findUserWithUserId(userId);
        validateAuthor(project, user);
    }

    public void validateAuthor(ProjectEntity project, UserEntity user) {
        if(project.getUser().getId().equals(user.getId()))
            return;

        if(user.isAdmin())
            return;

        throw new CustomException(ErrorCode.NOT_AUTHOR);
    }
}
