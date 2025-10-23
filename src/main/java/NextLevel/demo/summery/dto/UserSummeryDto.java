package NextLevel.demo.summery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserSummeryDto {

    private Long totalUserCount;
    private Long socialUserCount;
    private Long emailUserCount;
    private Long totalPoint;

    public UserSummeryDto(Long socialUserCount, Long emailUserCount, Long totalPoint) {
        this.socialUserCount = socialUserCount;
        this.emailUserCount = emailUserCount;
        this.totalUserCount = socialUserCount + emailUserCount;
        this.totalPoint = totalPoint;
    }
}
