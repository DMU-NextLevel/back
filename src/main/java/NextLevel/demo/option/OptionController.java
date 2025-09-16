package NextLevel.demo.option;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OptionController {

    private final OptionService optionService;

    @GetMapping("/public/option/{projectId}")
    public ResponseEntity<?> getAllOptions(@PathVariable("projectId") Long projectId) {
        List<ResponseOptionDto> dtos = optionService.getAllOptions(projectId);
        return ResponseEntity.ok().body(new SuccessResponse("success", dtos));
    }

    @PostMapping("/api1/option/{projectId}")
    public ResponseEntity<?> addOption(@PathVariable("projectId") Long projectId, @RequestBody @Valid SaveOptionRequestDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setProjectId(projectId);
        optionService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("success", null));
    }

    @PutMapping("/api1/option/{optionId}")
    public ResponseEntity<?> updateOption(@PathVariable("optionId") Long optionId, @RequestBody @Valid SaveOptionRequestDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        dto.setOptionId(optionId);
        optionService.update(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("success", null));
    }
    
    @DeleteMapping("/api1/option/{optionId}")
    public ResponseEntity<?> deleteOption(@PathVariable("optionId") Long optionId) {
        optionService.delete(optionId, JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("success", null));
    }
}
