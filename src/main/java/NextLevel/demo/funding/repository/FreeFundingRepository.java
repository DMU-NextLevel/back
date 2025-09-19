package NextLevel.demo.funding.repository;

import NextLevel.demo.funding.entity.FreeFundingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FreeFundingRepository extends JpaRepository<FreeFundingEntity, Long> {

    @Query("select sum(f.price) from FreeFundingEntity f where f.project.id = :projectId")
    Long getTotalPriceByProject(@Param("projectId") Long projectId);

    @Query("select count(ff) from FreeFundingEntity ff where ff.project.id = :projectId")
    Long getFundingCount(@Param("projectId") Long projectId);

    @Query("select ff from FreeFundingEntity ff where ff.user.id = :userId and ff.project.id = :projectId")
    Optional<FreeFundingEntity> findByProjectIdAndUserId(@Param("projectId") long projectId, @Param("userId") long userId);

    //for rollback funding
    @Query("select ff from FreeFundingEntity ff left join fetch ff.user where ff.project.id = :projectId")
    List<FreeFundingEntity> findAllWithUserByProject(@Param("projectId") Long projectId);

    @Query("select ff from FreeFundingEntity ff where ff.user.id = :userId")
    List<FreeFundingEntity> findAllByUser(@Param("userId") Long userId);

}
