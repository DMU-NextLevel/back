package NextLevel.demo.follow;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FollowDto {

    private Long targetId;
    private Boolean follow;

}
