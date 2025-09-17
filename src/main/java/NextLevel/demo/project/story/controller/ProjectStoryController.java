package NextLevel.demo.project.story.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.story.dto.ResponseProjectStoryListDto;
import NextLevel.demo.project.story.service.ProjectStoryService;
import NextLevel.demo.util.jwt.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProjectStoryController {

    private final ProjectStoryService projectStoryService;

    @PutMapping("/api1/project/{projectId}/story")
    public ResponseEntity<?> updateProjectStory(@PathVariable("projectId") long projectId, @RequestParam("imgs") List<MultipartFile> imgs){
        projectStoryService.updateProjectStory(projectId, JWTUtil.getUserIdFromSecurityContext() ,imgs, null);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success",null));
    }

    @GetMapping("/public/project/{projectId}/story")
    public ResponseEntity<?> getProjectStory(@PathVariable("projectId") long projectId){
        return ResponseEntity.ok().body(new SuccessResponse("success", new ResponseProjectStoryListDto(projectStoryService.getProjectStory(projectId))));
    }

}
