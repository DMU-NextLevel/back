package NextLevel.demo.funding.dto.response;

import NextLevel.demo.funding.entity.CouponEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseCouponDto {

    private Long id;
    private String name;
    private Long percent;

    public static ResponseCouponDto of(CouponEntity couponEntity) {
        if(couponEntity == null)
            return null;
        ResponseCouponDto dto = new ResponseCouponDto();
        dto.id = couponEntity.getId();
        dto.name = couponEntity.getName();
        dto.percent = couponEntity.getPrice();
        return dto;
    }
}
