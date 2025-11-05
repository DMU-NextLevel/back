package NextLevel.demo.summery.dto;

import java.time.LocalDateTime;
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
    private int year;

    public ProjectSummeryDto(LocalDateTime date, Long userCount, Long fundingPrice) {
        this.month = date.getMonth().getValue();
        this.year = date.getYear();
        this.userCount = userCount;
        this.fundingPrice = fundingPrice;
    }

}
