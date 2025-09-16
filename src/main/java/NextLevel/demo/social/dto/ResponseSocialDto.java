package NextLevel.demo.social.dto;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.social.entity.SocialEntity;
import NextLevel.demo.social.entity.SocialImgEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ResponseSocialDto {

    private Long id;
    private String text;
    private List<ImgDto> imgs;

    public static ResponseSocialDto of(SocialEntity entity) {
        ResponseSocialDto dto = new ResponseSocialDto();
        dto.id = entity.getId();
        dto.text = entity.getText();
        dto.imgs = entity.getImgs().stream().map(SocialImgEntity::getImg).map(ImgDto::new).toList();
        return dto;
    }

}
