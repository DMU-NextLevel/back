package NextLevel.demo.user.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.follow.FollowService;
import NextLevel.demo.project.project.dto.response.ProjectListWithFundingDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.service.MypageProjectSelectService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/social/user")
public class MypageController {

    private final MypageProjectSelectService mypageProjectSelectService;

    // 내가 좋아요한, 내가 최근 조회한, 내가 펀딩한(with funding) with tag
    @PostMapping("/project")
    public ResponseEntity<?> mypageProjectListSupporter(@RequestBody @Valid RequestMyPageProjectListDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", mypageProjectSelectService.mypageProjectList(dto)));
    }

    // 내가 생성한 project list(with funding) (default) // 아니 default는 with funding에서 잘 렌더링 해서 쓰라 그래(이것 때문에 col한개 더 받을수는 없잖아)
    @PostMapping("/project-withFunding")
    public ResponseEntity<?> myPageProjectListMaker(@RequestBody @Valid RequestMyPageProjectListDto dto) {
        dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("success", mypageProjectSelectService.mapageProjectListWithFunding(dto)));
    }
}
