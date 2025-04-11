package NextLevel.demo.project.project.repository;

import NextLevel.demo.project.project.entity.ProjectEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("select p from ProjectEntity p "
        + "left join fetch p.user u "
        + "left join fetch u.userDetail "
        + "left join fetch p.imgs "
        + "where p.id = :id")
    // only user create or update function
    Optional<ProjectEntity> findByIdWithAll(@Param("id") Long id);

    @Query("select p from ProjectEntity p left join fetch p.tags pt left join fetch pt.tag where p.id in :ids")
    List<ProjectEntity> findTagsByIds(@Param("ids") List<Long> ids);

    @Query("select p from ProjectEntity p "
        + "left join fetch p.user u "
        + "left join fetch p.fundings f "
        + "left join fetch p.recommends r "
        + "left join fetch p.communities c "
        + "left join fetch c.asker "
        + "left join fetch p.notices n "
        + "where p.id = :id")
    Optional<ProjectEntity> findProjectDetailById(@Param("id") Long id);

    @Query("select p from ProjectEntity p left join fetch p.notices left join fetch p.communities where p.id = :id")
    Optional<ProjectEntity> findProjectWithNoticesAndCommunity(@Param("id") Long id);

}
