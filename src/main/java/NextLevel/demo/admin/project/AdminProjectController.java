package NextLevel.demo.admin.project;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.project.project.service.ProjectService;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.service.MypageProjectSelectService;
import NextLevel.demo.user.service.UserService;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.util.jwt.JWTUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/project")
public class AdminProjectController {

    private final ProjectDeleteService projectDeleteService;
    private final AdminProjectService adminProjectService;
    private final MypageProjectSelectService mypageProjectListService;
    private final UserValidateService userValidateService;

    @GetMapping
    public ResponseEntity getAllProjectListWithFundingData(
            @RequestParam(value = "page", required = false) Long page,
            @RequestParam(value = "pageCount", required = false) Long pageCount
    ) {
        if(page == null)
            page = 0L;
        if(pageCount == null)
            pageCount = 10L;
        return ResponseEntity.ok().body(new SuccessResponse("success", adminProjectService.getAllProjectListWithFundingData(page, pageCount)));
    }

    @PostMapping
    public ResponseEntity<?> mypageProjectListSupporter(@RequestBody @Valid RequestMyPageProjectListDto dto) {
        userValidateService.findUserWithUserId(dto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", mypageProjectListService.mypageProjectList(dto)));
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

    @GetMapping("/funding")
    public ResponseEntity selectAllFunding(
            @RequestParam(value = "page", required = false) Long page,
            @RequestParam(value = "pageCount", required = false) Long pageCount
    ) {
        if(page == null)
            page = 0L;
        if(pageCount == null)
            pageCount = 10L;
        return ResponseEntity.ok().body(new SuccessResponse("success", adminProjectService.selectAllFunding(page, pageCount)));
    }
}
