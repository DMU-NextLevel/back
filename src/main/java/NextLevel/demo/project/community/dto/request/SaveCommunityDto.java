package NextLevel.demo.project.community.dto.request;

import NextLevel.demo.project.community.entity.ProjectCommunityAnswerEntity;
import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveCommunityDto {

    private Long id; // ask : project_id, answer : ask_id
    private Long userId;
    @NotEmpty
    private String content;

    public ProjectCommunityAskEntity toAskEntity(UserEntity user, ProjectEntity project) {
        return ProjectCommunityAskEntity
                .builder()
                .content(content)
                .user(user)
                .project(project)
                .build();
    }

    public ProjectCommunityAnswerEntity toAnswerEntity(UserEntity user, ProjectCommunityAskEntity ask) {
        return ProjectCommunityAnswerEntity
                .builder()
                .content(content)
                .user(user)
                .ask(ask)
                .build();
    }

}
