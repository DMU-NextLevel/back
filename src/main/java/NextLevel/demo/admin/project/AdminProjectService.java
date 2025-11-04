package NextLevel.demo.admin.project;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.dto.response.ProjectListWithFundingDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.project.project.service.ProjectStatusService;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectDeleteService projectDeleteService;
    private final ProjectStatusService projectStatusService;
    private final SelectProjectListDslRepository selectProjectListDslRepository;
    private final FundingDslRepository fundingDslRepository;

    @Transactional
    public ProjectListWithFundingDto getAllProjectListWithFundingData(Long page, Long pageCount) {
        ResponseProjectListDto projectListDto = selectProjectListDslRepository
                .builder(null)
                .limit(pageCount, page)
                .commit();

        List<ProjectEntity> projectList = projectListDto.getProjects().stream().map(ResponseProjectListDetailDto::getProjectEntity).toList();

        fundingDslRepository.addFundingData(projectList, null);

        return ProjectListWithFundingDto.of(projectListDto);
    }

    public void updateProjectStatus(Long projectId, ProjectStatus status) {
        projectStatusService.updateProjectStatus(projectId, status, "by admin");
    }

    public void updateProject() {

    }

    public void removeProject(Long projectId, Long userId) {
        projectDeleteService.deleteProject(projectId, userId,null);
    }

}
