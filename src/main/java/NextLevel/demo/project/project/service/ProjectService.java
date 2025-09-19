package NextLevel.demo.project.project.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.service.FundingValidateService;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.dto.request.CreateProjectDto;
import NextLevel.demo.project.project.dto.request.RequestMainPageProjectListDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectDslRepository;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.story.service.ProjectStoryService;
import NextLevel.demo.project.tag.service.TagService;
import NextLevel.demo.project.view.ProjectViewService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;

import java.nio.file.Path;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserValidateService userValidateService;

    private final ProjectViewService projectViewService;
    private final ImgServiceImpl imgService;
    private final TagService tagService;
    private final ProjectStoryService projectStoryService;

    private final FundingValidateService fundingValidateService;
    private final ProjectDslRepository projectDslRepository;
    private final ProjectValidateService projectValidateService;

    // 추가
    @ImgTransaction
    @Transactional
    public void save(CreateProjectDto dto, ArrayList<Path> imgPaths) {
        // user 처리
        UserEntity user = userValidateService.getUserInfo(dto.getUserId());
        validateUser(user);

        if(dto.getTitleImg() == null || dto.getTitleImg().isEmpty())
            throw new CustomException(ErrorCode.INPUT_REQUIRED_PARAMETER);
        ImgEntity img = imgService.saveImg(dto.getTitleImg(), imgPaths);

        ProjectEntity newProject = projectRepository.save(dto.toProjectEntity(user, img));

        projectStoryService.saveNewProjectStory(newProject, dto.getImgs(), imgPaths);
        tagService.saveNewTags(newProject, dto.getTags());
    }
    private void validateUser(UserEntity user) {
        // user 당 한달에 생성 가능한 펀딩 갯수 제한?
        // 일단 아무 일도 하지 않음 추후 수정 예정
    }

    // 수정
    @ImgTransaction
    @Transactional
    public void update(CreateProjectDto dto, ArrayList<Path> imgPaths) {
        Optional<ProjectEntity> oldProjectOptional = projectRepository.findByIdWithAll(dto.getId());

        if(oldProjectOptional.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND, "project");

        ProjectEntity oldProject = oldProjectOptional.get();

        if(oldProject.getUser().getId() != dto.getUserId())
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        ImgEntity img = oldProject.getTitleImg();
        if(dto.getTitleImg() != null)
            img = imgService.updateImg(dto.getTitleImg(), oldProject.getTitleImg(), imgPaths);

        // tag 처리
        if(dto.getTags() != null && !dto.getTags().isEmpty())
            tagService.updateTags(oldProject, dto.getTags());

        // img 처리
        if(dto.getImgs() != null && !dto.getImgs().isEmpty())
            projectStoryService.updateProjectStory(oldProject, dto.getImgs(), imgPaths);

        projectRepository.save(dto.toProjectEntity(oldProject.getUser(), img)); // 값이 있는 것만 update 형식으로 수정 필요
    }

    // 삭제
    public void deleteProject(Long id) {
        Optional<ProjectEntity> oldProjectOptional = projectRepository.findById(id);
        if(oldProjectOptional.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND, "project");
        ProjectEntity oldProject = oldProjectOptional.get();

        // 펀딩 금액이 남아있다면 모두 환불 처리하기

        // 다른 soft적 처리 필요한 부분 처리하기

        // img 처리

        return; // 아직 구현하지 않음 + soft delete 처리 고민중 .....
    }

    // get list
    public ResponseProjectListDto getAllProjects(RequestMainPageProjectListDto dto) {
        return projectDslRepository.selectProjectDsl(dto);
    }

    @Transactional
    public ResponseProjectDetailDto getProjectDetailById(Long id, Long userId) {
        ProjectEntity project = projectRepository.findProjectDetailById(id).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND, "project")
        );

        projectViewService.save(project, userId);
        Long fundingPrice = fundingValidateService.getTotalFundingPrice(project.getId());
        Long fundingCount = fundingValidateService.getTotalFundingCount(project.getId());

        return ResponseProjectDetailDto.of(project, fundingPrice, fundingCount, userId);
    }

}
