package NextLevel.demo.project.project.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.service.FundingRollbackService;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.notice.service.ProjectNoticeService;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.story.service.ProjectStoryService;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectDeleteService {

    private final ProjectRepository projectRepository;
    private final ProjectStoryService projectStoryService;
    private final ProjectValidateService projectValidateService;
    private final FundingRollbackService fundingRollbackService;
    private final ProjectNoticeService projectNoticeService;
    private final ImgService imgService;

    @PersistenceContext
    private EntityManager entityManager;

    // 삭제
    @Transactional
    @ImgTransaction
    public void deleteProject(Long id, Long userId, ImgPath imgPath) {
        ProjectEntity project = projectValidateService.validateAuthor(id, userId);

        // 펀딩 금액이 남아있다면 모두 환불 처리하기
        fundingRollbackService.rollbackByProject(project);

        // project like, community, tag, view, option -> cascade 적용

        // notice
        projectNoticeService.deleteAllProjectNotice(project.getId(), null);

        // img 처리
        projectStoryService.updateProjectStory(project, new ArrayList<>(), imgPath);

        log.info(String.valueOf(project.getStories().size()));

        project.getStories().clear();

        log.info(String.valueOf(project.getStories().size()));

        entityManager.flush();
        entityManager.clear();

        project = projectValidateService.getProjectEntity(id);
        ImgEntity titleImg = project.getTitleImg();

        projectRepository.deleteById(project.getId());

        imgService.deleteImg(titleImg, imgPath);
    }

    @Transactional
    public void deleteAllProjectByUser(UserEntity user, ImgPath imgPath) {
        List<ProjectEntity> projectList = projectRepository.findAllByUserId(user.getId());
        projectList.forEach(project -> {
            deleteProject(project.getId(), user.getId(), imgPath);
        });
    }

}
