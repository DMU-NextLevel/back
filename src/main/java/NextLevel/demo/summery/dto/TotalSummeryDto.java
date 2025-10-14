package NextLevel.demo.summery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TotalSummeryDto {

    private Long totalFundingPrice;
    private Long totalFundingCount;
    private Long totalSuccessProjectCount;
    private Long totalSupporterCount;
    private Long totalCreatorCount;

    public TotalSummeryDto(Long totalFundingPrice, Long totalFundingCount, Long totalSuccessProjectCount, Long totalSupporterCount, Long totalCreatorCount) {
        this.totalFundingPrice = totalFundingPrice;
        this.totalFundingCount = totalFundingCount;
        this.totalSuccessProjectCount = totalSuccessProjectCount;
        this.totalSupporterCount = totalSupporterCount;
        this.totalCreatorCount = totalCreatorCount;
    }

}
