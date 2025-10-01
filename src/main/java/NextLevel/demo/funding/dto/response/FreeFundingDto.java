package NextLevel.demo.funding.dto.response;

import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.user.dto.user.response.UserFundingInfoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class FreeFundingDto {

    private UserFundingInfoDto user;
    private Long price;
    private LocalDateTime createdAt;

    public static FreeFundingDto of(FreeFundingEntity freeFunding) {
        if(freeFunding == null) return null;
        FreeFundingDto dto = new FreeFundingDto();
        dto.user = UserFundingInfoDto.of(freeFunding.getUser());
        dto.price = freeFunding.getPrice();
        dto.createdAt = freeFunding.getCreatedAt();
        return dto;
    }

    public static FreeFundingDto of(Optional<FreeFundingEntity> freeFunding) {
        if(freeFunding == null || freeFunding.isEmpty()) return null;
        return FreeFundingDto.of(freeFunding.get());
    }

}
