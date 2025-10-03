package NextLevel.demo.project.project.dto.request;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class CreateProjectDto {
    private Long id;
    private Long userId;

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotEmpty
    private String expired;
    @NotNull
    private Long goal;

    private List<Long> tags = new ArrayList<>();

    private MultipartFile titleImg;

    private List<MultipartFile> imgs = new ArrayList<>();

    public ProjectEntity toProjectEntity(UserEntity user, ImgEntity titleImg) {
        try {
            return ProjectEntity.builder()
                .id(id)
                .user(user)
                .title(title)
                .content(content)
                .titleImg(titleImg)
                .expiredAt(expired)
                .goal(goal)
                .build();
        }catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.ERROR_EXPIRED_DATE_CONVERSION, expired);
        }
    }
}
