package NextLevel.demo.admin.coupon;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.dto.request.RequestAddCouponDto;
import NextLevel.demo.funding.dto.response.ResponseCouponDto;
import NextLevel.demo.funding.service.CouponService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.UserValidateService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/admin/coupon")
@Controller
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;
    private final UserValidateService validateService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity addCoupon(@RequestBody @Valid RequestAddCouponDto dto) {
        if(dto.getUserId()==null)
            throw new CustomException(ErrorCode.NOT_FOUND, "user");
        couponService.addCoupon(dto);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @GetMapping
    public ResponseEntity getUserCoupon(@RequestParam("userId")Long userId) {
        validateService.findUserWithUserId(userId);
        return ResponseEntity.ok().body(new SuccessResponse("success", couponService.couponList(userId).stream().map(
                ResponseCouponDto::of).toList()));
    }

    @PostMapping("/all")
    @Transactional
    public ResponseEntity addCouponAll(@RequestBody @Valid RequestAddCouponDto dto) {
        List<UserEntity> userList = userRepository.findAll();
        for(UserEntity user : userList) {
            dto.setUserId(user.getId());
            couponService.addCoupon(dto);
        }

        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

}
