package NextLevel.demo.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    @Query("select f from FollowEntity f where f.user.id = :userId and f.target.id = :targetId")
    Optional<FollowEntity> findByUserIdAndTargetId(@Param("userId") Long userId, @Param("targetId") Long targetId);

}
