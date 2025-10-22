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
    private Long totalProgressProjectCount;
    private Long totalSupporterCount;
    private Long totalCreatorCount;

    public TotalSummeryDto(
            Long totalFundingPrice,
            Long totalFundingCount,
            Long totalSuccessProjectCount,
            Long totalProgressProjectCount,
            Long totalSupporterCount,
            Long totalCreatorCount
    ) {
        this.totalFundingPrice = totalFundingPrice != null ? totalFundingPrice : 0L;
        this.totalFundingCount = totalFundingCount !=  null ? totalFundingCount : 0L;
        this.totalSuccessProjectCount = totalSuccessProjectCount != null ? totalSuccessProjectCount : 0L;
        this.totalProgressProjectCount = totalProgressProjectCount != null ? totalProgressProjectCount : 0L;
        this.totalSupporterCount = totalSupporterCount != null ? totalSupporterCount : 0L;
        this.totalCreatorCount = totalCreatorCount != null ? totalCreatorCount : 0L;
    }

}
