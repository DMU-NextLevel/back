package NextLevel.demo.summery.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProjectSummeryDto {

    private Integer month;
    private Long userCount;
    private Long fundingPrice;

    public ProjectSummeryDto(Integer month, Long userCount, Long fundingPrice) {
        this.month = month;
        this.userCount = userCount;
        this.fundingPrice = fundingPrice;
    }

}
