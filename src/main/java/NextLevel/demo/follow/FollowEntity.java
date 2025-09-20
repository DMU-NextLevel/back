package NextLevel.demo.follow;

import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follow" , uniqueConstraints = {@UniqueConstraint(name="likeUniqueConstraint", columnNames = {"user_id", "target_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private UserEntity target;

}
