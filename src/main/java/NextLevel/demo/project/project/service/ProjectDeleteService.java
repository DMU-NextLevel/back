package NextLevel.demo.project.project.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.service.FundingRollbackService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.story.service.ProjectStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectDeleteService {

    private final ProjectRepository projectRepository;
    private final ProjectStoryService projectStoryService;
    private final ProjectValidateService projectValidateService;
    private final FundingRollbackService fundingRollbackService;

    // 삭제
    @Transactional
    @ImgTransaction
    public void deleteProject(Long id, ArrayList<Path> imgPaths) {
        ProjectEntity oldProject = projectValidateService.getProjectEntity(id);

        // 펀딩 금액이 남아있다면 모두 환불 처리하기
        fundingRollbackService.rollbackByProject(oldProject);

        // 다른 soft적 처리 필요한 부분 처리하기

        // img 처리
        projectStoryService.updateProjectStory(oldProject, new ArrayList<>(), imgPaths);

        return; // 아직 구현하지 않음 + soft delete 처리 고민중 .....
    }

}
