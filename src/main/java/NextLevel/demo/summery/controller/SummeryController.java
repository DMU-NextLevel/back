package NextLevel.demo.summery.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.summery.service.SummeryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SummeryController {

    private final SummeryService summeryService;

    @GetMapping("/public/summery/mainpage")
    public ResponseEntity getMainPageSummery() {
        return ResponseEntity.ok().body(new SuccessResponse("success", summeryService.projectSummery()));
    }

}
