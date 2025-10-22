package NextLevel.demo.admin.project;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.util.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectDeleteService projectDeleteService;
    private final AdminProjectService adminProjectService;

    public ResponseEntity getProjectList() {
        return null;
    }

    public ResponseEntity updateProjectStatus() {
        return null;
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
