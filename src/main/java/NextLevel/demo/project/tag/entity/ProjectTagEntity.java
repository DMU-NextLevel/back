package NextLevel.demo.project.tag.entity;

import NextLevel.demo.project.project.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(targetEntity = TagEntity.class, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
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
