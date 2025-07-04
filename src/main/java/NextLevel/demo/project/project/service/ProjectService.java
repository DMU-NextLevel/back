package NextLevel.demo.project.project.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.dto.response.FundingResponseDto;
import NextLevel.demo.funding.entity.FundingEntity;
import NextLevel.demo.funding.entity.OptionEntity;
import NextLevel.demo.funding.repository.FundingRepository;
import NextLevel.demo.funding.repository.OptionRepository;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.dto.request.CreateProjectDto;
import NextLevel.demo.project.project.dto.request.SelectProjectListRequestDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectDslRepository;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.project.entity.ProjectTagEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.tag.service.TagService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ImgService imgService;
    private final TagService tagService;
    private final OptionRepository optionRepository;
    private final FundingRepository fundingRepository;
    private final ProjectViewService projectViewService;
    private final ProjectDslRepository projectDslRepository;

    // 추가
    @ImgTransaction
    @Transactional
    public void save(CreateProjectDto dto, ArrayList<Path> imgPaths) {
        // user 처리
        UserEntity user = userService.getUserInfo(dto.getUserId());
        validateUser(user);
        dto.setUser(user);

        ImgEntity img = null;
        try{
            img = imgService.saveImg(dto.getTitleImg(), imgPaths);
        }catch (CustomException e){;}
        dto.setTitleImgEntity(img);

        ProjectEntity newProject = dto.toEntity();

        // img 처리
        List<ImgEntity> imgEntitys = new ArrayList<>();
        dto.getImgs().forEach(imgEntity -> {imgEntitys.add(imgService.saveImg(imgEntity, imgPaths));});

        newProject.setStories(
            imgEntitys.stream().map((e)->{
                return ProjectStoryEntity
                    .builder()
                    .project(newProject)
                    .img(e)
                    .build();
            }).collect(Collectors.toSet())
        );

        // tag 처리
        newProject.setTags(tagService.getTagEntitysByIds(dto.getTags())
            .stream()
            .map((t)->{
                return ProjectTagEntity
                    .builder()
                    .project(newProject)
                    .tag(t)
                    .build();
            }).toList());

        projectRepository.save(newProject);
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

        dto.setUser(oldProject.getUser());

        if(dto.getTitleImg() == null)
            dto.setTitleImgEntity(oldProject.getTitleImg());
        else
            dto.setTitleImgEntity(imgService.updateImg(dto.getTitleImg(), oldProject.getTitleImg(), imgPaths));

        ProjectEntity newProject = dto.toEntity();

        // tag 처리
        newProject.setTags(tagService.getTagEntitysByIds(dto.getTags())
            .stream()
            .map((t)->{
                return ProjectTagEntity
                    .builder()
                    .project(newProject)
                    .tag(t)
                    .build();
            })
            .toList()
        );

        // img 처리
        if(dto.getImgs() != null) {
            List<ImgEntity> imgEntitys = new ArrayList<>();
            dto.getImgs().forEach(imgEntity -> {
                imgEntitys.add(imgService.saveImg(imgEntity, imgPaths));
            });

            newProject.setStories(
                imgEntitys.stream().map((e) -> {
                    return ProjectStoryEntity
                        .builder()
                        .project(newProject)
                        .img(e)
                        .build();
                }).collect(Collectors.toSet())
            );
        }else{
            newProject.setStories(oldProject.getStories());
        }

        // 여기 부분 다시 수정해라 ㅅㅂ 이미지 값이 없어도 무조건 삭제 잖아
        
        List<ImgEntity> oldImgs = oldProject.getStories().stream().map(pe -> pe.getImg()).toList();
        oldImgs.forEach(i->{ imgService.deleteImg(i);});

        projectRepository.save(newProject);
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
    public ResponseProjectListDto getAllProjects(SelectProjectListRequestDto dto) {
        List<ResponseProjectListDetailDto> detailDtos = projectDslRepository.selectProjectDsl(dto);

        Map<Long, ResponseProjectListDetailDto> dtoMap = new HashMap<>();
        detailDtos.forEach(e -> {dtoMap.put(e.getId(), e);});

        List<ProjectEntity> tags = projectRepository.findTagsByIds(dtoMap.keySet().stream().toList());
        tags.forEach((e)->
            dtoMap.get(e.getId()).setTags(
                e.getTags().stream().map(pt->pt.getTag().getName()).toList()
            )
        );

        ResponseProjectListDto resultDto = new ResponseProjectListDto(detailDtos);
        resultDto.setPageCount(dto.getPageCount(), dto.getPage());

        return resultDto;
    }

    @Transactional
    public ResponseProjectDetailDto getProjectDetailById(Long id, Long userId) {
        ProjectEntity project = projectRepository.findProjectDetailById(id).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND, "project")
        );

        projectViewService.save(project, userId);

        return ResponseProjectDetailDto.of(project, userId);
    }

    // notice and community and story
    public ProjectEntity getProjectCommunityAndNoticeAndStoryById(Long id) {
        return projectRepository.findProjectWithNoticesAndCommunityAndStory(id).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND,"project")
        );
    }

    @Transactional
    public List<FundingResponseDto> getAllOptionWithFunding(Long projectId, Long userId) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND, "project")
        );
        if(project.getUser().getId() != userId)
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        List<OptionEntity> options = optionRepository.findByProjectIdWithAll(projectId);
        List<FundingEntity> freeFundings = fundingRepository.findByProjectIdAndOptionIdIsNull(project.getId());

        List<FundingResponseDto> dto = new ArrayList<>(options.stream().map(FundingResponseDto::new).toList());
        dto.add(new FundingResponseDto(freeFundings.stream().map(FundingEntity::getUser).toList()));

        return  dto;
    }

}
