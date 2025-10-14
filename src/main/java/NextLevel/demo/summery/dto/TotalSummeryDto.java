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
<<<<<<< HEAD
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
        this.totalFundingPrice = totalFundingPrice;
        this.totalFundingCount = totalFundingCount;
        this.totalSuccessProjectCount = totalSuccessProjectCount;
        this.totalProgressProjectCount = totalProgressProjectCount;
=======
    private Long totalSupporterCount;
    private Long totalCreatorCount;

    public TotalSummeryDto(Long totalFundingPrice, Long totalFundingCount, Long totalSuccessProjectCount, Long totalSupporterCount, Long totalCreatorCount) {
        this.totalFundingPrice = totalFundingPrice;
        this.totalFundingCount = totalFundingCount;
        this.totalSuccessProjectCount = totalSuccessProjectCount;
>>>>>>> a0b38d9 (feat summery : summery)
        this.totalSupporterCount = totalSupporterCount;
        this.totalCreatorCount = totalCreatorCount;
    }

}
