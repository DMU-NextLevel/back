package NextLevel.demo.social.repository;

import NextLevel.demo.social.entity.SocialLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialLikeRepository extends JpaRepository<SocialLikeEntity, Long> {
    Optional<SocialLikeEntity> findByUserIdAndSocialId(Long userId, Long socialId);

    @Modifying
    @Query("delete from SocialLikeEntity sl where sl.user.id = :userId")
    void deleteAllByUser(@Param("userId")Long userId);
}
