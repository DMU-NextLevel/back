package NextLevel.demo.project.batch;

import NextLevel.demo.project.project.entity.ProjectEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProjectSerializableDto implements Serializable {

    private Long projectId;
    private Long projectGoal;

    public static ProjectSerializableDto of(ProjectEntity projectEntity) {
        ProjectSerializableDto projectSerializableDto = new ProjectSerializableDto();
        projectSerializableDto.setProjectId(projectEntity.getId());
        projectSerializableDto.setProjectGoal(projectEntity.getGoal());
        return projectSerializableDto;
    }

}
