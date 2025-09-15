package NextLevel.demo.social.repository;

import NextLevel.demo.social.entity.SocialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialRepository extends JpaRepository<SocialEntity, Long> {

    @Query("select s from SocialEntity s left join fetch s.imgs where s.user.id = :userId order by s.createdAt desc")
    List<SocialEntity> findAllByUserId(@Param("userId") Long userId);

}
