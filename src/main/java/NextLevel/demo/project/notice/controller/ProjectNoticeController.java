package NextLevel.demo.project.notice.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.notice.dto.request.SaveProjectNoticeRequestDto;
import NextLevel.demo.project.notice.dto.response.ResponseNoticeListDto;
import NextLevel.demo.project.notice.dto.response.ResponseProjectNoticeDto;
import NextLevel.demo.project.notice.service.ProjectNoticeService;
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
public class ProjectNoticeController {

    private final ProjectNoticeService projectNoticeService;

    // select notice by project
    @GetMapping("/public/project/{projectId}/notice")
    public ResponseEntity<?> getProjectNotice(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(new SuccessResponse("success", new ResponseNoticeListDto(projectNoticeService.getAllNotice(projectId))));
    }

    @PostMapping("/api1/project/{projectId}/notice")
    public ResponseEntity<?> addProjectNotice(@PathVariable("projectId") long projectId, @ModelAttribute @Valid SaveProjectNoticeRequestDto dto) {
        dto.setProjectId(projectId);
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        projectNoticeService.saveProjectNotice(dto, null);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @PutMapping("/api1/project/notice/{noticeId}")
    public ResponseEntity<?> updateProjectNotice(@PathVariable("noticeId") long noticeId, @ModelAttribute @Valid SaveProjectNoticeRequestDto dto) {
        dto.setNoticeId(noticeId);
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        projectNoticeService.updateNotice(dto, null);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }

    @DeleteMapping("/api1/project/notice/{noticeId}")
    public ResponseEntity<?> deleteProjectNotice(@PathVariable("noticeId") long noticeId){
        projectNoticeService.deleteProjectNotice(noticeId, JWTUtil.getUserIdFromSecurityContext(), null);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", null));
    }
}
