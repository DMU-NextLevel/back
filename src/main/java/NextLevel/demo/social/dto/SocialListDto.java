package NextLevel.demo.social.dto;

import NextLevel.demo.user.dto.user.response.UserSocialProfileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SocialListDto {

    private UserSocialProfileDto user;
    private List<ResponseSocialDto> socials;

    public static SocialListDto of(UserSocialProfileDto user, List<ResponseSocialDto> socials) {
        SocialListDto dto = new SocialListDto();
        dto.user = user;
        dto.socials = socials;
        return dto;
    }

}
