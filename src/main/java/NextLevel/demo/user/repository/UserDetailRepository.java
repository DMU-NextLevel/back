package NextLevel.demo.user.repository;

import NextLevel.demo.user.entity.UserDetailEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {

    @Query("select ud "
        + "from UserDetailEntity ud "
            + "left join fetch ud.user u "
        + "where ud.socialProvider=:socialProvider and ud.socialId = :socialId")
    Optional<UserDetailEntity> findBySocialProviderAndSocialId(@Param("socialProvider") String socialProvider, @Param("socialId") String socialId);

    Optional<UserDetailEntity> findByEmail(String email);

    @Query("update UserDetailEntity u set u.password = :password where u.user.id = :userId")
    @Modifying
    void updatePasswordByUserId(@Param("password") String password, @Param("userId") Long userId);
}
