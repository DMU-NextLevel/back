package NextLevel.demo.user.repository;

import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.entity.UserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistoryEntity, Long> {

    void deleteAllByUser(UserEntity user);
}
