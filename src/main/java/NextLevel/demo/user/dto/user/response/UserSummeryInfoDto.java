package NextLevel.demo.user.dto.user.response;

import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserSummeryInfoDto {

    private String name;
    private String nickName;

    public static UserSummeryInfoDto of(UserEntity user) {
        UserSummeryInfoDto dto = new UserSummeryInfoDto();
        dto.name = user.getName();
        dto.nickName = user.getNickName();
        return dto;
    }

}
