package NextLevel.demo.funding.service;

import NextLevel.demo.funding.repository.FreeFundingRepository;
import NextLevel.demo.funding.repository.OptionFundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundingValidateService {
    private final OptionFundingRepository optionFundingRepository;
    private final FreeFundingRepository freeFundingRepository;

    // validate not exist funding (Option option) // return boolean or return void and throw?
    // validate not exist funding (Project project) // db 조회인데 구지 option validate service에 둘 이유가 없어 보임

    public Long getTotalFundingPrice(Long projectId) {
        Long optionFundingPrice = optionFundingRepository.getTotalPriceByProject(projectId);
        Long freeFundingPrice = freeFundingRepository.getTotalPriceByProject(projectId);
        return (optionFundingPrice != null ? optionFundingPrice : 0)
                + (freeFundingPrice != null ? freeFundingPrice : 0);
    }

    public Long getTotalFundingCount(Long projectId) {
        Long optionFundingPrice = optionFundingRepository.getTotalFundingCount(projectId);
        Long freeFundingPrice = freeFundingRepository.getFundingCountByProject(projectId);
        return (optionFundingPrice != null ? optionFundingPrice : 0)
                + (freeFundingPrice != null ? freeFundingPrice : 0);
    }

}
