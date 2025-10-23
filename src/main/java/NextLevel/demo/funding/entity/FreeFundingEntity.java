package NextLevel.demo.funding.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "free_funding")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@SQLDelete(sql="update free_funding set delete_at = now() where id = ?")
@SQLRestriction("delete_at IS NULL")
public class FreeFundingEntity extends BasedEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long price;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(targetEntity = ProjectEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    public void updatePrice(long price) {this.price += price;}

}
