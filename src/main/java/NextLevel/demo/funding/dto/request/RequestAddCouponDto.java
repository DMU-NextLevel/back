package NextLevel.demo.funding.dto.request;

import NextLevel.demo.funding.entity.CouponEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RequestAddCouponDto {

    private Long userId;
    @NotEmpty
    private String name;
    @NotNull
    private Long price;

    public CouponEntity toEntity(UserEntity user) {
        return CouponEntity
                .builder()
                .name(name)
                .price(price)
                .user(user)
                .build();
    }

}
