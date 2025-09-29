package NextLevel.demo.user.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.project.project.dto.response.ProjectListDetailWithFundingDto;
import NextLevel.demo.project.project.dto.response.ProjectListWithFundingDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.service.MypageProjectSelectService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/social/user")
public class MypageController {

    private final MypageProjectSelectService mypageProjectSelectService;

    // 내가 좋아요한, 내가 최근 조회한 with tag
    @PostMapping("/project")
    public ResponseEntity<?> mypageProjectListSupporter(@RequestBody @Valid RequestMyPageProjectListDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", mypageProjectSelectService.mypageProjectList(dto)));
    }

    // 내가 펀딩한 펀딩 리스트 with tag (분리 사유 : 반환값에 내 펀딩 정보 또한 같이 반환 필요)
    @PostMapping("/project/my-funding")
    public ResponseEntity<?> myFundingProjectList(@RequestBody @Valid RequestMyPageProjectListDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", ProjectListWithFundingDto.of(mypageProjectSelectService.mypageProjectList(dto))));
    }

}
