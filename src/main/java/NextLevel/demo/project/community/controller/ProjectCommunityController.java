package NextLevel.demo.project.community.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.project.community.dto.response.ResponseProjectCommunityDto;
import NextLevel.demo.project.community.service.ProjectCommunityService;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.util.jwt.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProjectCommunityController {

    private final ProjectService projectService;
    private final ProjectCommunityService projectCommunityService;

    // 생성만
    @PostMapping("/api1/project/{projectId}/community")
    public ResponseEntity<?> saveProjectCommunity(@PathVariable("projectId") Long projectId, @RequestBody SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setProjectId(projectId);
        projectCommunityService.save(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // 수정
    @PostMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> updateProjectCommunity(@PathVariable("communityId") Long communityId, @RequestBody SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(communityId);
        projectCommunityService.save(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> deleteProjectCommunity(@PathVariable("communityId") Long communityId) {
        projectCommunityService.delete(communityId);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }
}
