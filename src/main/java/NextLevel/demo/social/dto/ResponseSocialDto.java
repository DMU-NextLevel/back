package NextLevel.demo.social.dto;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.social.entity.SocialEntity;
import NextLevel.demo.social.entity.SocialImgEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long socialLikeCount;
    @JsonProperty("isLiked")
    private boolean isSocialLike; // 내 좋아요 여부

    public ResponseSocialDto(SocialEntity entity, Long totalLikeCount, Long myLikeCount) {
        id = entity.getId();
        text = entity.getText();
        imgs = entity.getImgs().stream().map(SocialImgEntity::getImg).map(ImgDto::new).toList();
        socialLikeCount = totalLikeCount != null ? totalLikeCount : 0L;
        isSocialLike = myLikeCount != null && myLikeCount.equals(1L);
    }

}
