package NextLevel.demo.funding.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.dto.request.RequestAddCouponDto;
import NextLevel.demo.funding.entity.CouponEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.funding.repository.CouponRepository;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserValidateService userValidateService;

    @Transactional
    public void addCoupon(RequestAddCouponDto dto) {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        couponRepository.save(dto.toEntity(user));
    }

    public List<CouponEntity> couponList(long userId) {
        return couponRepository.findByUserIdAndOptionFundingIsNull(userId);
    }

    @Transactional
    public long useCoupon(long userId, long couponId, OptionFundingEntity optionFunding, long price) {
        CouponEntity coupon = couponRepository.findById(couponId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "coupon");}
        );

        if(!coupon.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        if(coupon.getOptionFunding() != null)
            throw new CustomException(ErrorCode.ALREADY_USED_COUPON);

        price -= coupon.getPrice();

        coupon.updateProjectFundingEntity(optionFunding);

        return price>0?price:0;
    }

}
