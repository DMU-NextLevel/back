package NextLevel.demo.project.community.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_community_answer")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class ProjectCommunityAnswerEntity extends BasedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne(targetEntity = ProjectCommunityAskEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ask_id", nullable = false)
    private ProjectCommunityAskEntity ask;

    public void update(SaveCommunityDto dto) {
        if(dto.getContent() != null && !dto.getContent().isEmpty())
            this.content = dto.getContent();
    }

}
