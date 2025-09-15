package NextLevel.demo.funding.repository;

import NextLevel.demo.funding.entity.OptionFundingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionFundingRepository extends JpaRepository<OptionFundingEntity, Long> {

    @Query("select sum(f.count * f.option.price) " +
            "from OptionEntity o left join o.fundings f " +
            "where o.project.id = :projectId " +
            "group by f")
    Long getTotalPriceByProject(@Param("projectId") Long projectId);

    @Query("select sum(of.count) " +
            "from OptionFundingEntity of " +
            "where of.option.project.id = :projectId ")
    Long getTotalFundingCount(@Param("projectId") Long projectId);

}
