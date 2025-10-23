package NextLevel.demo.project.view;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name="project_view")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql="update project_view set delete_at = now() where id = ?")
@SQLRestriction("delete_at IS NULL")
public class ProjectViewEntity extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Builder
    public ProjectViewEntity(Long id, ProjectEntity project, UserEntity user) {
        this.id = id;
        this.project = project;
        this.user = user;
        this.createAt = LocalDateTime.now();
    }
}
