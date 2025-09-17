package NextLevel.demo.project.community.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.project.community.dto.response.ResponseCommunityListDto;
import NextLevel.demo.project.community.service.ProjectCommunityAskService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProjectCommunityController {

    private final ProjectCommunityAskService communityService;

    // list
    @GetMapping("/public/project/{projectId}/community")
    public ResponseEntity getProjectCommunity(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", new ResponseCommunityListDto(communityService.selectAll(projectId))));
    }

    // 생성만
    @PostMapping("/api1/project/{projectId}/community")
    public ResponseEntity<?> saveProjectCommunity(@PathVariable("projectId") Long projectId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(projectId);
        communityService.create(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // 수정
    @PostMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> updateProjectCommunity(@PathVariable("communityId") Long communityId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(communityId);
        communityService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> deleteProjectCommunity(@PathVariable("communityId") Long communityId) {
        communityService.delete(communityId,JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }
}
