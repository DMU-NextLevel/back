package NextLevel.demo.project.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BatchController {

    private final ProjectBatchService projectBatchService;

    @GetMapping("/public/batch")
    public ResponseEntity doBatch() {
        projectBatchService.runProjectStatusJob();
        return ResponseEntity.ok().build();
    }

}
