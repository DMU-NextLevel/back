package NextLevel.demo.user.dto.user.response;

import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserFundingInfoDto {

    private Long id;

    private String name;
    private String nickName;

    private String number;
    private String areaNumber;
    private String address;

    public static UserFundingInfoDto of(UserEntity user) {
        UserFundingInfoDto dto = new UserFundingInfoDto();
        dto.id = user.getId();
        dto.setName(user.getName());
        dto.setNickName(user.getNickName());
        dto.setNumber(user.getNumber());
        dto.setAreaNumber(user.getAreaNumber());
        dto.setAddress(user.getAddress());
        return dto;
    }
}
