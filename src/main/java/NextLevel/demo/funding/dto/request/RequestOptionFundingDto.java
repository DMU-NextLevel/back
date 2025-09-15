package NextLevel.demo.funding.dto.request;

import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestOptionFundingDto {

    @NotNull
    private Long optionId;
    private Long count = 1L;
    private Long couponId;

    private Long userId;

    public OptionFundingEntity toEntity(UserEntity user, OptionEntity option) {
        return OptionFundingEntity
                .builder()
                .option(option)
                .user(user)
                .count(count)
                .build();
    }

}
