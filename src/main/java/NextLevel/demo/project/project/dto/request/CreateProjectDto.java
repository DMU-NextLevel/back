package NextLevel.demo.project.project.dto.request;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.user.entity.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import NextLevel.demo.util.StringUtil;
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
    private String expiredAt;
    @NotNull
    private Long goal;

    private String startAt; // 시작일 default today

    private List<Long> tags = new ArrayList<>();

    private MultipartFile titleImg;

    private List<MultipartFile> imgs = new ArrayList<>();

    public ProjectEntity toProjectEntity(UserEntity user, ImgEntity titleImg) {
        return ProjectEntity.builder()
                .id(id)
                .user(user)
                .title(title)
                .content(content)
                .titleImg(titleImg)
                .expiredAt(StringUtil.toLocalDate(expiredAt))
                .startAt(startAt !=null?StringUtil.toLocalDate(startAt):LocalDate.now())
                .goal(goal)
                .build();
    }

}
