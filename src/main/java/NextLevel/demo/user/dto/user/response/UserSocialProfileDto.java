package NextLevel.demo.user.dto.user.response;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
public class UserSocialProfileDto {

    private Long id;

    private String name;
    private String nickName;
    private Long followCount;
    private ImgDto img;
    @JsonProperty("isFollow")
    private boolean isFollow;

    public static UserSocialProfileDto of(UserEntity user, Long followCount, Long isFollow) {
        UserSocialProfileDto dto = new UserSocialProfileDto();
        dto.id = user.getId();
        dto.followCount = followCount;
        dto.name = user.getName();
        dto.nickName = user.getNickName();
        dto.img = new ImgDto(user.getImg());
        dto.isFollow = isFollow!=null?isFollow.equals(1L):false;
        return dto;
    }

}
