package NextLevel.demo.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    @Query("select f from FollowEntity f where f.user.id = :userId and f.target.id = :targetId")
    Optional<FollowEntity> findByUserIdAndTargetId(@Param("userId") Long userId, @Param("targetId") Long targetId);

    @Query("select " +
                "(select count(f1) from FollowEntity f1 where f1.target.id = :targetUserId) as followCount, " +
                "(select count(f2) from FollowEntity f2 where f2.target.id = :targetUserId and f2.user.id = :userId) as isFollow " +
            "from TagEntity t " +
            "where t.id = 1 ")
    SelectFollowCountAndIsFollowDao selectFollowCountAndFollowDao(@Param("targetUserId") Long targetUserId, @Param("userId") Long userId);
}
