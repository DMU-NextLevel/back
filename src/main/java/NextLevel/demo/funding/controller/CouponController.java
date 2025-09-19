package NextLevel.demo.funding.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.funding.dto.request.RequestAddCouponDto;
import NextLevel.demo.funding.dto.response.ResponseCouponDto;
import NextLevel.demo.funding.service.CouponService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/admin/coupon/add")
    public ResponseEntity addCoupon(@RequestBody @Valid RequestAddCouponDto dto) {
        if(dto.getUserId()==null)
            dto.setUserId(JWTUtil.getUserIdFromSecurityContext());
        couponService.addCoupon(dto);
        return ResponseEntity.ok().body(new SuccessResponse("success", null));
    }

    @GetMapping("/social/coupon")
    public ResponseEntity getCoupon() {
        List<ResponseCouponDto> coupons =
                couponService.couponList(JWTUtil.getUserIdFromSecurityContext())
                        .stream().map(ResponseCouponDto::of).toList();
        return ResponseEntity.ok().body(new SuccessResponse("success", coupons));
    }

}
