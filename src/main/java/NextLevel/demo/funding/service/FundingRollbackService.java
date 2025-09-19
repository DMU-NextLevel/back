package NextLevel.demo.funding.service;

import NextLevel.demo.funding.entity.CouponEntity;
import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.funding.repository.CouponRepository;
import NextLevel.demo.funding.repository.FreeFundingRepository;
import NextLevel.demo.funding.repository.OptionFundingRepository;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundingRollbackService {

    private final CouponRepository couponRepository;
    private final OptionFundingRepository optionFundingRepository;
    private final FreeFundingRepository freeFundingRepository;

    private long rollbackCoupon(CouponEntity coupon, long price) {
        price -= coupon.getPrice();
        coupon.rollBackUseCoupon();
        couponRepository.save(coupon); // transaction내부에서는 필요 없음? (transaction에 영속성 밖이라 적용이 안될려나?)
        return price>0?price:0;
    }

    public void rollbackOptionFunding(UserEntity user, OptionFundingEntity optionFunding) {
        long price = optionFunding.getCount() * optionFunding.getOption().getPrice();

        if(optionFunding.getCoupon() != null){
            price = rollbackCoupon(optionFunding.getCoupon(), price);
        }

        optionFundingRepository.deleteById(optionFunding.getId());
        user.updatePoint(+price);
    }

    public void rollbackFreeFunding(UserEntity user, FreeFundingEntity freeFunding) {
        long price = freeFunding.getPrice();
        freeFundingRepository.deleteById(freeFunding.getId());
        user.updatePoint(+price);
    }

    // option 삭제 / 변경 시 사용 (option의 funding들만 rollback 시켜줌
    public void rollbackByOption(OptionEntity option) {
        // option의 funding의 모든 user에게
        List<OptionFundingEntity> optionFundingList = optionFundingRepository.findAllWithUserByOption(option.getId());
        optionFundingList.forEach((funding)-> rollbackOptionFunding(funding.getUser(), funding));
    }

    // project 삭제시 사용
    public void rollbackByProject(ProjectEntity project) {
        List<OptionFundingEntity> optionFundingList = optionFundingRepository.findAllWithUserByProject(project.getId());
        List<FreeFundingEntity> freeFundingList = freeFundingRepository.findAllWithUserByProject(project.getId());

        optionFundingList.forEach(optionFunding -> rollbackOptionFunding(optionFunding.getUser(), optionFunding));
        freeFundingList.forEach(freeFunding -> rollbackFreeFunding(freeFunding.getUser(), freeFunding));
    }

    // user의 모든 funding을 rollback 시킴 (user delete시 data 무결성 때문에 rollback기능은 필요함!)
    public void rollbackByUser(UserEntity user) {
        List<OptionFundingEntity> optionFundingList = optionFundingRepository.findAllByUser(user.getId());
        List<FreeFundingEntity> freeFundingList = freeFundingRepository.findAllByUser(user.getId());

        optionFundingList.forEach(optionFunding -> rollbackOptionFunding(user, optionFunding));
        freeFundingList.forEach(freeFunding -> rollbackFreeFunding(user, freeFunding));
    }

}
