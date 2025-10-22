package NextLevel.demo.funding.repository;

import NextLevel.demo.funding.entity.CouponEntity;
import NextLevel.demo.funding.entity.OptionFundingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    @Query("select c from CouponEntity c where c.user.id = :userId and c.optionFunding.option.id = :optionFundingId")
    Optional<CouponEntity> findByUserIdAndOptionFundingId(@Param("userId") Long userId, @Param("optionFundingId") Long optionFundingId);

    @Query("select c from CouponEntity c where c.optionFunding is null and c.user.id = :userId")
    List<CouponEntity> findByUserIdAndOptionFundingIsNull(@Param("userId") Long userId);
}
