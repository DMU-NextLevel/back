package NextLevel.demo.social.repository;

import NextLevel.demo.social.dto.ResponseSocialDto;
import NextLevel.demo.social.entity.SocialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialRepository extends JpaRepository<SocialEntity, Long> {

    @Query("select s " +
            "from SocialEntity s " +
              "left join fetch s.imgs " +
            "where s.user.id = :targetUserId " +
            "order by s.createdAt desc "
    )
    List<SocialEntity> findAllByUserId(@Param("targetUserId") Long targetUserId);

    @Query("select sl.id as socialId, count(distinct total_like) as totalLikeCount, count(distinct my_like) as myLikeCount " +
            "from SocialEntity sl " +
                "left join SocialLikeEntity total_like on total_like.social.id = sl.id " +
                "left join SocialLikeEntity my_like on my_like.social.id = sl.id and my_like.user.id = :userId " +
            "where sl.id in :socialIds " +
            "group by sl ")
    List<IsSelectSocialLikedListInterface> isSelectSocialLikeList(@Param("socialIds") List<Long> socialIds, @Param("userId") Long userId);

}
