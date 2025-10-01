package NextLevel.demo.funding.dto.response;

import NextLevel.demo.funding.entity.CouponEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.user.dto.user.response.UserFundingInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
//option funding dto : user funding Info dto, 갯수
public class OptionFundingDto {

    private UserFundingInfoDto user;
    private ResponseCouponDto coupon;
    private LocalDateTime createdAt;
    private long price;
    private Long count;

    public static OptionFundingDto of(OptionFundingEntity optionFunding) {
        OptionFundingDto dto = new OptionFundingDto();
        dto.user = UserFundingInfoDto.of(optionFunding.getUser());
        dto.coupon = ResponseCouponDto.of(optionFunding.getCoupon());
        dto.createdAt = optionFunding.getCreatedAt();
        dto.count = optionFunding.getCount();
        dto.price = ( optionFunding.getOption().getPrice() * dto.count ) - (optionFunding.getCoupon() != null ? optionFunding.getCoupon().getPrice() : 0);
        return dto;
    }

}
