package NextLevel.demo.project.community.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.project.community.dto.response.ResponseCommunityListDto;
import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import NextLevel.demo.project.community.repository.ProjectCommunityAskRepository;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectCommunityAskService {

    private final ProjectValidateService projectValidateService;
    private final ProjectCommunityAskRepository projectCommunityAskRepository;
    private final EntityManager entityManager;

    @Transactional
    public void create(SaveCommunityDto dto) {
        Long projectId = dto.getId();
        ProjectEntity project = projectValidateService.getProjectEntity(projectId);

        UserEntity user = entityManager.getReference(UserEntity.class, dto.getUserId());

        projectCommunityAskRepository.save(dto.toAskEntity(user, project));
    }

    @Transactional
    public void update(SaveCommunityDto dto) {
        Long askId = dto.getId();
        ProjectCommunityAskEntity ask = projectCommunityAskRepository.findById(askId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "community");}
        );

        if(ask.getUser().getId() != dto.getUserId())
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        ask.update(dto);
    }

    @Transactional
    public ResponseCommunityListDto selectAll(Long projectId) {
        ProjectEntity project = projectValidateService.getProjectEntity(projectId);
        List<ProjectCommunityAskEntity> asks = projectCommunityAskRepository.findAllWithAnswerByProjectId(project.getId());
        return new ResponseCommunityListDto(asks);
    }

    @Transactional
    public void delete(Long askId, Long userId) {
        ProjectCommunityAskEntity ask = projectCommunityAskRepository.findById(askId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "community");}
        );

        if(! (ask.getUser().getId() == userId) || (ask.getProject().getId() == userId) ) // project validate service 를 통해야 하나?
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        projectCommunityAskRepository.deleteById(askId);
    }

}
