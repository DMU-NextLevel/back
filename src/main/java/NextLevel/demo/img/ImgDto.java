package NextLevel.demo.img;

import NextLevel.demo.img.entity.ImgEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class ImgDto {

    private static final String DEFAULT_IMG_URI = "very_very_long_and_long_default_img.png";

    private Long id;
    private String uri;

    public ImgDto(ImgEntity imgEntity) {
        if(imgEntity == null) {
            id = null;
            uri = DEFAULT_IMG_URI;
            return;
        }

        this.id = imgEntity.getId();
        this.uri = imgEntity.getUri();
    }

}
