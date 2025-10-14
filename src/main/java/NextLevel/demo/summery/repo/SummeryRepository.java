package NextLevel.demo.summery.repo;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.user.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface SummeryRepository extends Repository<UserEntity, Long> {

    @Query("select sum(f.price) from FreeFundingEntity f")
    long getTotalFreeFundingPrice();

    @Query("select count(f) from FreeFundingEntity f")
    long getFreeFundingCount();

    @Query("select sum(of.count * of.option.price - coalesce(of.coupon.price, 0)) from OptionFundingEntity of")
    long getTotalOptionFundingPrice();

    @Query("select count(of) from OptionFundingEntity of")
    long getTotalOptionFundingCount();

    @Query("select count(p) from ProjectEntity p where p.projectStatus in :status")
    long getProjectCount(@Param("status") List<ProjectStatus> status);

    @Query("select count(distinct u) from UserEntity u " +
            "left join ProjectEntity p on p.user.id = u.id " +
            "where p.id is not null")
    long getCreatorCount();

    @Query("select count(distinct u) from UserEntity u " +
            "left join FreeFundingEntity ff on ff.user.id = u.id " +
            "left join OptionFundingEntity of on of.user.id = u.id " +
            "where ff.id is not null or of.id is not null")
    long getSupporterCount();

}
