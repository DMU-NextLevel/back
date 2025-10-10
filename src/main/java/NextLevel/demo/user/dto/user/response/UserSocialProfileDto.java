package NextLevel.demo.user.dto.user.response;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
public class UserSocialProfileDto {

    private String name;
    private String nickName;
    private Long followCount;
    private ImgDto img;

    public static UserSocialProfileDto of(UserEntity user, Long followCount) {
        UserSocialProfileDto dto = new UserSocialProfileDto();
        dto.followCount = followCount;
        dto.name = user.getName();
        dto.nickName = user.getNickName();
        dto.img = new ImgDto(user.getImg());
        return dto;
    }

}
