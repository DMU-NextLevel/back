package NextLevel.demo.summery.repo;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
//org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param;

<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> a0b38d9 (feat summery : summery)
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

<<<<<<< HEAD
    @Query("select count(p) from ProjectEntity p where p.projectStatus in :status")
    long getProjectCount(@Param("status") List<ProjectStatus> status);
=======
    @Query("select count(p) from ProjectEntity p where p.projectStatus = :status")
    long getProjectCount(@Param("status")ProjectStatus status);
>>>>>>> a0b38d9 (feat summery : summery)

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
