package NextLevel.demo.social.repository;

import NextLevel.demo.social.entity.SocialImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialImgRepository extends JpaRepository<SocialImgEntity, Long> {

    @Modifying
    @Query("delete from SocialImgEntity si where si.social.id = :socialId")
    void deleteAllBySocialId(@Param("socialId") Long socialId);
}
