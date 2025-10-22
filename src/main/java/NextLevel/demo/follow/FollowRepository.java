package NextLevel.demo.follow;

import NextLevel.demo.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("select new NextLevel.demo.follow.ResponseFollowDto(" +
                "follower , " +
                "(select count(f1) from FollowEntity f1 where f1.target.id = follower.id and f1.user.id = :targetId) as isFollow " +
            ")" +
            "from FollowEntity f " +
                "left join f.user follower " +
                "left join fetch follower.img " +
            "where f.target.id = :targetId " +
            "group by f.user")
    List<ResponseFollowDto> gerFollowerList(@Param("targetId") Long targetId);

    @Query("select new NextLevel.demo.follow.ResponseFollowDto(" +
                "follow , " +
                "(select count(f1) from FollowEntity f1 where f1.user.id = follow.id and f1.target.id = :userId) as isFollow " +
            ")" +
                "follow " +
            "from FollowEntity f " +
                "left join f.target follow " +
                "left join fetch follow.img " +
            "where f.user.id = :userId " +
            "group by follow")
    List<ResponseFollowDto> gerFollowList(@Param("userId") Long userId);

}
