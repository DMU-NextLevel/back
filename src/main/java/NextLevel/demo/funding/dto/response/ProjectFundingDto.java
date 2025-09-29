package NextLevel.demo.funding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 기본 funding 정보 (총 금액, 총 funding 수)
public class ProjectFundingDto {
    private Long count;
    private Long price;
}
