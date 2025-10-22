package NextLevel.demo.follow;

import NextLevel.demo.user.dto.user.response.UserProfileDto;
import NextLevel.demo.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseFollowDto {

    private UserProfileDto user;
    @JsonProperty("isFollow")
    private boolean isFollow;

    public ResponseFollowDto (UserEntity user, Long isFollow) {
        this.user = UserProfileDto.of(user);
        this.isFollow = isFollow!=null?isFollow.equals(1L):false;
    }

}
