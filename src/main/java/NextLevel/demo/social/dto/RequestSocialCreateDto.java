package NextLevel.demo.social.dto;

import NextLevel.demo.social.entity.SocialEntity;
import NextLevel.demo.social.entity.SocialImgEntity;
import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RequestSocialCreateDto {

    private Long id;
    private String text;
    private List<MultipartFile> imgs;

    private Long userId;

    public SocialEntity toEntity(UserEntity user) {
        return SocialEntity
                .builder()
                .text(text)
                .user(user)
                .build();
    }

}
