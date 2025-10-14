package NextLevel.demo.user.dto.user.response;

import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserSummeryInfoDto {

    private Long id;

    private String name;
    private String nickName;

    public static UserSummeryInfoDto of(UserEntity user) {
        UserSummeryInfoDto dto = new UserSummeryInfoDto();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.nickName = user.getNickName();
        return dto;
    }

}
