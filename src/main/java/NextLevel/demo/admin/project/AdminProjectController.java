package NextLevel.demo.admin.project;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.util.jwt.JWTUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectDeleteService projectDeleteService;
    private final AdminProjectService adminProjectService;

    public ResponseEntity getProjectList() {
        return null;
    }

    @PostMapping("/status/{projectId}")
    public ResponseEntity updateProjectStatus(@RequestParam("status") ProjectStatus status, @PathVariable("projectId") Long projectId) {
        adminProjectService.updateProjectStatus(projectId, status);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    public ResponseEntity updateProject() {
        return null;
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity removeProject(@PathVariable("projectId") Long projectId) {
        projectDeleteService.deleteProject(projectId, JWTUtil.getUserIdFromSecurityContext(), null);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }
}
