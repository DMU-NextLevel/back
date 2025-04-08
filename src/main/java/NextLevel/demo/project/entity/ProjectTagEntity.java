package NextLevel.demo.project.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="project_tag")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectTagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = TagEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Override
    public String toString() {
        return "ProjectTagEntity{" +
            "id=" + id +
            ", tag=" + tag +
            '}';
    }
}
