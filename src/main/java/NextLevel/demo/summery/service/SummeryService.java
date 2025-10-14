package NextLevel.demo.summery.service;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.summery.dto.TotalSummeryDto;
import NextLevel.demo.summery.repo.SummeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummeryService {

    private final SummeryRepository summeryRepository;

    @Transactional
    public TotalSummeryDto projectSummery() {
        Long totalFundingPrice = summeryRepository.getTotalFreeFundingPrice() + summeryRepository.getTotalOptionFundingPrice();
        Long totalFundingCount = summeryRepository.getFreeFundingCount() + summeryRepository.getTotalOptionFundingCount();
        Long totalSuccessProjectCount = summeryRepository.getProjectCount(List.of(ProjectStatus.SUCCESS));
        Long totalProgressProjectCount = summeryRepository.getProjectCount(List.of(ProjectStatus.PROGRESS));
        Long totalSupporterCount = summeryRepository.getSupporterCount();
        Long totalCreatorCount = summeryRepository.getCreatorCount();

        return new TotalSummeryDto(totalFundingPrice, totalFundingCount, totalSuccessProjectCount, totalProgressProjectCount, totalSupporterCount, totalCreatorCount);
    }

}
