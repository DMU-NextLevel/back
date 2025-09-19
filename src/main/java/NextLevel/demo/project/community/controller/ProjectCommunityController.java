package NextLevel.demo.project.community.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.project.community.service.ProjectCommunityAnswerService;
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

    private final ProjectCommunityAskService askService;
    private final ProjectCommunityAnswerService answerService;

    // ask 관련

    // list
    @GetMapping("/public/project/{projectId}/community")
    public ResponseEntity getProjectCommunity(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", new ResponseCommunityListDto(askService.selectAll(projectId))));
    }

    // 생성
    @PostMapping("/api1/project/{projectId}/community")
    public ResponseEntity<?> saveProjectCommunityAsk(@PathVariable("projectId") Long projectId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(projectId);
        askService.create(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // 수정
    @PostMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> updateProjectCommunityAsk(@PathVariable("communityId") Long communityId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(communityId);
        askService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/project/community/{communityId}")
    public ResponseEntity<?> deleteProjectCommunity(@PathVariable("communityId") Long communityId) {
        askService.delete(communityId,JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // answer 관련

    // 생성
    @PostMapping("/api1/project/{askId}/community/answer")
    public ResponseEntity<?> saveProjectCommunityAnswer(@PathVariable("askId") Long askId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(askId);
        answerService.addAnswer(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    // 수정
    @PostMapping("/api1/project/community/{answerId}/answer")
    public ResponseEntity<?> updateProjectCommunityAnswer(@PathVariable("answerId") Long answerId, @RequestBody @Valid SaveCommunityDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setId(answerId);
        answerService.updateAnswer(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/project/community/{answerId}/answer")
    public ResponseEntity<?> deleteProjectCommunityAnswer(@PathVariable("answerId") Long answerId) {
        answerService.deleteAnswer(answerId,JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

}
