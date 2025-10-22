package NextLevel.demo.social.repository;

import NextLevel.demo.social.entity.SocialLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialLikeRepository extends JpaRepository<SocialLikeEntity, Long> {
    Optional<SocialLikeEntity> findByUserIdAndSocialId(Long userId, Long socialId);
}
