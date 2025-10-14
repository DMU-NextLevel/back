package NextLevel.demo.funding.repository;

import NextLevel.demo.funding.entity.OptionFundingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionFundingRepository extends JpaRepository<OptionFundingEntity, Long> {

    @Query("select sum(of.count * of.option.price) " +
            "from OptionFundingEntity of " +
            "where of.option.project.id = :projectId ")
    Long getTotalPriceByProject(@Param("projectId") Long projectId);

    @Query("select sum(of.count) " +
            "from OptionFundingEntity of " +
            "where of.option.project.id = :projectId ")
    Long getTotalFundingCount(@Param("projectId") Long projectId);

    @Query("select of from OptionFundingEntity of where of.option.id = :optionId and of.user.id = :userId")
    Optional<OptionFundingEntity> findByOptionIdAndUserId(@Param("optionId") long optionId, @Param("userId") long userId);

    // for rollback funding
    @Query("select of from OptionFundingEntity of left join fetch of.user where of.option.id = :optionId")
    List<OptionFundingEntity> findAllWithUserByOption(@Param("optionId") Long optionId);

    @Query("select of from OptionFundingEntity of left join fetch of.user where of.option.project.id = :projectId")
    List<OptionFundingEntity> findAllWithUserByProject(@Param("projectId") Long projectId);

    @Query("select of from OptionFundingEntity of where of.user.id = :userId")
    List<OptionFundingEntity> findAllByUser(@Param("userId") Long userId);
}
