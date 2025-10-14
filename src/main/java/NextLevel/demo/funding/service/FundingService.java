package NextLevel.demo.funding.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.dto.request.RequestCancelFundingDto;
import NextLevel.demo.funding.dto.request.RequestFreeFundingDto;
import NextLevel.demo.funding.dto.request.RequestOptionFundingDto;
import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.funding.repository.FreeFundingRepository;
import NextLevel.demo.funding.repository.OptionFundingRepository;
import NextLevel.demo.option.OptionValidateService;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class FundingService {

    private final UserValidateService userValidateService;
    private final ProjectValidateService projectValidateService;
    private final OptionValidateService optionValidateService;

    private final OptionFundingRepository optionFundingRepository;
    private final FreeFundingRepository freeFundingRepository;

    private final FundingRollbackService fundingRollbackService;

    private final CouponService couponService;

    @Transactional
    public void cancelFreeFunding(RequestCancelFundingDto dto) {
        Long projectId = dto.getId();
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        FreeFundingEntity funding = freeFundingRepository.findByProjectIdAndUserId(projectId, dto.getUserId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "freeFunding");}
        );

        fundingRollbackService.rollbackFreeFunding(user, funding);
    }

    @Transactional
    public void cancelOptionFunding(RequestCancelFundingDto dto) {
        Long optionId = dto.getId();
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        OptionFundingEntity funding = optionFundingRepository.findByOptionIdAndUserId(optionId, dto.getUserId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "optionFunding");}
        );

        fundingRollbackService.rollbackOptionFunding(user, funding);
    }

    @Transactional
    public void optionFunding(@Valid RequestOptionFundingDto dto) {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        OptionEntity option = optionValidateService.getOption(dto.getOptionId()); // option id가 null인 경우

        OptionFundingEntity entity;

        Optional<OptionFundingEntity> oldOptionFundingOpt = optionFundingRepository.findByOptionIdAndUserId(dto.getOptionId(), dto.getUserId());
        if(oldOptionFundingOpt.isPresent())
            entity = oldOptionFundingOpt.get();
        else
            entity = dto.toEntity(user, option);

        // validate price <> option.price * count
        long totalPrice = option.getPrice() * dto.getCount();
        if(dto.getCouponId() != null)
            totalPrice = couponService.useCoupon(user.getId(), dto.getCouponId(), entity, totalPrice); // option update인 경우 이미 coupon을 사용했다면 rollback발생?

        if(totalPrice > user.getPoint())
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT, String.valueOf(user.getPoint()), String.valueOf(totalPrice));

        if(oldOptionFundingOpt.isPresent())
            entity.updateCount(dto.getCount());
        else
            optionFundingRepository.save(entity);

        user.updatePoint(-totalPrice);
    }

    @Transactional
    public void freeFunding(@Valid RequestFreeFundingDto dto) {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        ProjectEntity project = projectValidateService.getProjectEntity(dto.getProjectId());

        if(dto.getFreePrice() > user.getPoint())
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT, String.valueOf(user.getPoint()), String.valueOf(dto.getFreePrice()));

        Optional<FreeFundingEntity> oldFreeFundingOpt = freeFundingRepository.findByProjectIdAndUserId(project.getId(), dto.getUserId());

        if(oldFreeFundingOpt.isPresent())
            oldFreeFundingOpt.get().updatePrice(dto.getFreePrice());
        else
            freeFundingRepository.save(dto.toEntity(user, project));

        user.updatePoint(-dto.getFreePrice());
    }

}
