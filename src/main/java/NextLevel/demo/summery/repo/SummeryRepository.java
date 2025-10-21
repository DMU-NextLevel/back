package NextLevel.demo.summery.repo;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.user.entity.UserEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface SummeryRepository extends Repository<UserEntity, Long> {

    // ---------------total summery ---------------

    @Query("select sum(f.price) from FreeFundingEntity f")
    Long getTotalFreeFundingPrice();

    @Query("select count(f) from FreeFundingEntity f")
    Long getFreeFundingCount();

    @Query("select sum(of.count * of.option.price) from OptionFundingEntity of")
    Long getTotalOptionFundingPrice();

    @Query("select count(of) from OptionFundingEntity of")
    Long getTotalOptionFundingCount();

    @Query("select count(p) from ProjectEntity p where p.projectStatus in :status")
    Long getProjectCount(@Param("status") List<ProjectStatus> status);

    @Query("select count(distinct u) from UserEntity u " +
            "left join ProjectEntity p on p.user.id = u.id " +
            "where p.id is not null")
    Long getCreatorCount();

    @Query("select count(distinct u) from UserEntity u " +
            "left join FreeFundingEntity ff on ff.user.id = u.id " +
            "left join OptionFundingEntity of on of.user.id = u.id " +
            "where ff.id is not null or of.id is not null")
    Long getSupporterCount();

    // ------------------------------------- project summery -------------------------------

    @Query("select sum(of.count * of.option.price) "
            + "from OptionFundingEntity of "
            + "where of.option.project.id = :projectId and of.createdAt >= :start and of.createdAt <= :end")
    Long getOptionFundingPrice(@Param("projectId") long projectId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select sum(ff.price) "
            + "from FreeFundingEntity ff "
            + "where ff.project.id = :projectId and ff.createdAt >= :start and ff.createdAt <= :end")
    Long getFreeFundingPrice(@Param("projectId") long projectId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select count(of.id) "
            + "from OptionFundingEntity of "
            + "where of.option.project.id = :projectId and of.createdAt >= :start and of.createdAt <= :end")
    Long getOptionFundingCount(@Param("projectId") long projectId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select count(ff.id) "
            + "from FreeFundingEntity ff "
            + "where ff.project.id = :projectId and ff.createdAt >= :start and ff.createdAt <= :end")
    Long getFreeFundingCount(@Param("projectId") long projectId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
