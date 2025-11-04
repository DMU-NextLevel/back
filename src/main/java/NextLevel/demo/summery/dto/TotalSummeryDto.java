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
            Long totalFreeFundingPrice,
            Long totalOptionFundingPrice,
            Long freeFundingCount,
            Long totalOptionFundingCount,
            Long totalSuccessProjectCount,
            Long totalProgressProjectCount,
            Long totalSupporterCount,
            Long totalCreatorCount
    ) {
        // null 체크 후 덧셈 연산
        this.totalFundingPrice = (totalFreeFundingPrice != null ? totalFreeFundingPrice : 0L) 
                + (totalOptionFundingPrice != null ? totalOptionFundingPrice : 0L);
        this.totalFundingCount = (freeFundingCount != null ? freeFundingCount : 0L) 
                + (totalOptionFundingCount != null ? totalOptionFundingCount : 0L);
        
        // 나머지는 null 체크만
        this.totalSuccessProjectCount = totalSuccessProjectCount != null ? totalSuccessProjectCount : 0L;
        this.totalProgressProjectCount = totalProgressProjectCount != null ? totalProgressProjectCount : 0L;
        this.totalSupporterCount = totalSupporterCount != null ? totalSupporterCount : 0L;
        this.totalCreatorCount = totalCreatorCount != null ? totalCreatorCount : 0L;
    }

}
