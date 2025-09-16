package NextLevel.demo.project.notice.dto.request;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SaveProjectNoticeRequestDto {
    private Long noticeId;
    private Long projectId;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    private MultipartFile img;

    private Long userId;

    public ProjectNoticeEntity toEntity(ImgEntity imgEntity, ProjectEntity projectEntity ) {
        return ProjectNoticeEntity.builder()
            .id(noticeId)
            .title(title)
            .content(content)
            .img(imgEntity)
            .project(projectEntity)
            .build();
    }
}
