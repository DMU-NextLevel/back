package NextLevel.demo.summery.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.summery.service.SummeryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SummeryController {

    private final SummeryService summeryService;

    @GetMapping("/public/summery/project/{projectId}")
    public ResponseEntity getProjectSummery(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok().body( new SuccessResponse("success", summeryService.projectSummery(projectId)));
    }

    @GetMapping("/public/summery/mainpage")
    public ResponseEntity getMainPageSummery() {
        return ResponseEntity.ok().body(new SuccessResponse("success", summeryService.totalSummery()));
    }

    @GetMapping("/public/summery/user")
    public ResponseEntity getUserSummery() {
        return ResponseEntity.ok().body(new SuccessResponse("success", summeryService.userSummery()));
    }

}
