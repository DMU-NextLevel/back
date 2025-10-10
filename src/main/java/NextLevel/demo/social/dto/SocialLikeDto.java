package NextLevel.demo.social.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SocialLikeDto {

    @NotNull
    private Long socialId;

    private Long userId;

    @NotNull
    private Boolean like;
}
