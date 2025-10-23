package NextLevel.demo.summery.service;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.summery.dto.ProjectSummeryDto;
import NextLevel.demo.summery.dto.TotalSummeryDto;
import NextLevel.demo.summery.dto.UserSummeryDto;
import NextLevel.demo.summery.repo.SummeryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummeryService {

    private final SummeryRepository summeryRepository;
    private final ProjectValidateService projectValidateService;

    @Transactional
    public TotalSummeryDto totalSummery() {
        Long totalFundingPrice = summeryRepository.getTotalFreeFundingPrice() + summeryRepository.getTotalOptionFundingPrice();
        Long totalFundingCount = summeryRepository.getFreeFundingCount() + summeryRepository.getTotalOptionFundingCount();
        Long totalSuccessProjectCount = summeryRepository.getProjectCount(List.of(ProjectStatus.SUCCESS));
        Long totalProgressProjectCount = summeryRepository.getProjectCount(List.of(ProjectStatus.PROGRESS));
        Long totalSupporterCount = summeryRepository.getSupporterCount();
        Long totalCreatorCount = summeryRepository.getCreatorCount();

        return new TotalSummeryDto(totalFundingPrice, totalFundingCount, totalSuccessProjectCount, totalProgressProjectCount, totalSupporterCount, totalCreatorCount);
    }

    @Transactional
    public List<ProjectSummeryDto> projectSummery(Long projectId) {
        projectValidateService.getProjectEntity(projectId);

        List<ProjectSummeryDto> dtoList = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            LocalDate now = LocalDate.now().minusMonths(i);
            LocalDateTime start = now.atStartOfDay();
            LocalDateTime end = now.plusMonths(1).atStartOfDay().minusDays(1).withHour(23).withMinute(59).withSecond(59);

            dtoList.add(getProjectSummeryDto(projectId, start, end));
        }

        return dtoList;
    }

    private ProjectSummeryDto getProjectSummeryDto(long projectId, LocalDateTime start, LocalDateTime end) {
        Long totalFreePrice = summeryRepository.getFreeFundingPrice(projectId, start, end);
        Long totalOptionPrice = summeryRepository.getOptionFundingPrice(projectId, start, end);
        Long totalFreeUserCount = summeryRepository.getFreeFundingCount(projectId, start, end);
        Long totalOptionUserCount = summeryRepository.getOptionFundingCount(projectId, start, end);

        Long totalUserCount =
                (totalFreeUserCount != null ? totalFreeUserCount : 0L) +
                (totalOptionUserCount != null ? totalOptionUserCount : 0L);

        Long totalFundingPrice = (totalFreePrice != null ? totalFreePrice : 0L) + (totalOptionPrice != null ? totalOptionPrice : 0L);

        return new ProjectSummeryDto(start.getMonthValue(), totalUserCount, totalFundingPrice);
    }

    @Transactional
    public UserSummeryDto userSummery() {
        Long emailUserCount = summeryRepository.getEmailUserCount();
        Long socialUserCount = summeryRepository.getSocialUserCount();
        Long totalPoint = summeryRepository.getTotalPoint();

        return new UserSummeryDto(socialUserCount, emailUserCount, totalPoint);
    }

}
