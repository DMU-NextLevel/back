package NextLevel.demo.social.entity;

import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_like" , uniqueConstraints = {@UniqueConstraint(name="socialLikeUniqueConstraint", columnNames = {"user_id", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SocialLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "social_id", nullable = false)
    private SocialEntity social;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
