package NextLevel.demo.user.repository;

import NextLevel.demo.user.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u left join fetch u.userDetail where u.id = :userId")
    Optional<UserEntity> findUserFullInfoByUserId(@Param("userId")Long userId);

    @Query("select u from UserEntity u where u.nickName = :nickName")
    List<UserEntity> findUserByNickName(@Param("nickName") String nickName);

    @Query("select u from UserEntity u left join fetch u.userDetail d where d.email = :email")
    Optional<UserEntity> findUserByEmail(@Param("email") String email);

    @Modifying
    @Query("update UserEntity u set u.point = u.point + :point where u.id = :userId")
    Integer addPointByUserId(@Param("point")int point, @Param("userId") Long userId);

    @Modifying
    @Query("update UserEntity u set u.point = u.point - :point where u.id = :userId")
    void minusPointByUserId(@Param("point")int point, @Param("userId") Long userId);

}
