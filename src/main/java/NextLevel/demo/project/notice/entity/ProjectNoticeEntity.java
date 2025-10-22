package NextLevel.demo.project.notice.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.notice.dto.request.SaveProjectNoticeRequestDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

import lombok.*;

@Entity
@Table(name = "project_notice")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProjectNoticeEntity extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column
    private String title;
    @Column
    private String content;

    @ManyToOne(targetEntity = ImgEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private ImgEntity img;

    public void update(SaveProjectNoticeRequestDto dto) {
        if(dto.getContent() != null && !dto.getContent().isEmpty())
            content = dto.getContent();
        if(dto.getTitle() != null && !dto.getTitle().isEmpty())
            title = dto.getTitle();
    }
}
