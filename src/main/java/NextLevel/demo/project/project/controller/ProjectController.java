package NextLevel.demo.project.project.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.funding.dto.response.FundingResponseDto;
import NextLevel.demo.project.community.dto.response.ResponseCommunityListDto;
import NextLevel.demo.project.notoce.dto.response.ResponseNoticeListDto;
import NextLevel.demo.project.project.dto.request.CreateProjectDto;
import NextLevel.demo.project.project.dto.request.SelectProjectListRequestDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectAllDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectOrderType;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.project.story.dto.ResponseProjectStoryListDto;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ProjectController {

    private final ProjectService projectService;

    // 추가
    @PostMapping("/api1/project")
    public ResponseEntity<?> createNewProject(@ModelAttribute CreateProjectDto dto) {
        Long userId = JWTUtil.getUserIdFromSecurityContext();
        dto.setUserId(userId);

        projectService.save(dto, null);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("success", null));
    }

    // 수정
    @PutMapping("/api1/project/{projectId}")
    public ResponseEntity<?> updateProject(@ModelAttribute @Valid CreateProjectDto dto, @PathVariable("projectId") Long projectId) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(projectId);

        projectService.update(dto, null);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 모두 조회
    @GetMapping("/public/project/all")
    public ResponseEntity<?> getAllProjects(
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "tag", required = false) List<Long> tagId,
            @RequestParam(value = "page", required = false) Integer page ,
            @RequestParam(value = "search", required = false) String search ,
            @RequestParam(value = "desc", required = false) Boolean desc)
    {
        Long userId = JWTUtil.getUserIdFromSecurityContextCanNULL();

        SelectProjectListRequestDto dto = SelectProjectListRequestDto.builder()
            .tag(tagId)
            .page(page)
            .order(order)
            .userId(userId)
            .search(search)
            .desc(desc)
            .build();

        ResponseProjectListDto resultDto = projectService.getAllProjects(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success" ,resultDto));
    }

    @PostMapping("/public/project/all")
    public ResponseEntity<?> getAllProjects(
        @RequestBody SelectProjectListRequestDto dto)
    {
        Long userId = JWTUtil.getUserIdFromSecurityContextCanNULL();
        dto.setUserId(userId);

        ResponseProjectListDto resultDto = projectService.getAllProjects(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success" ,resultDto));
    }

    // 상세 조회
    @GetMapping("/public/project/{projectId}")
    public ResponseEntity<?> getProjectDetailById(@PathVariable("projectId") Long projectId) {
        Long userId = JWTUtil.getUserIdFromSecurityContextCanNULL();

        ResponseProjectDetailDto dto = projectService.getProjectDetailById(projectId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success" ,dto));
    }

    @GetMapping("/public/project/{projectId}/all")
    public ResponseEntity<?> getProjectNotice(@PathVariable("projectId") Long projectId) {
        ProjectEntity project = projectService.getProjectCommunityAndNoticeAndStoryById(projectId);

        ResponseProjectAllDto dto = ResponseProjectAllDto.builder()
            .story(new ResponseProjectStoryListDto(project.getStories()))
            .notice(new ResponseNoticeListDto(project.getNotices()))
            .community(new ResponseCommunityListDto(project.getCommunities()))
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", dto));
    }

    @GetMapping("/api1/project/{projectId}/funding")
    public ResponseEntity<?> getAllFundings(@PathVariable("projectId") Long projectId) {
        List<FundingResponseDto> dto = projectService.getAllOptionWithFunding(projectId, JWTUtil.getUserIdFromSecurityContext());

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", dto));
    }

}
