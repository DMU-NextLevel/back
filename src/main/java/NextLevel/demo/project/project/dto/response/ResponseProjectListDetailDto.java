package NextLevel.demo.project.project.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.tag.entity.TagEntity;
import NextLevel.demo.user.dto.user.response.UserProfileDto;
import NextLevel.demo.user.dto.user.response.UserSummeryInfoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseProjectListDetailDto {
    private Long id;
    private String title;

    private UserSummeryInfoDto author;

    private ImgDto titleImg;

    private Double completionRate;

    private Long likeCount;

    private List<String> tags;
    
    private Long userCount;
    private Long viewCount;

    private LocalDateTime createdAt;

    private Boolean isLiked;
    private LocalDate expiredAt;
    private LocalDate startAt;

    private String status;

    @JsonIgnore
    private LocalDateTime projectViewCreateAt;
    @JsonIgnore
    private ProjectEntity projectEntity;

    public ResponseProjectListDetailDto(
        ProjectEntity projectEntity,

        Double completionRate,
        long likeCount,
        long userCount,
        Long isLiked,
        long viewCount
        // LocalDateTime projectViewCreateAt // select distinct 용 column
    ) {
        this.id = projectEntity.getId();
        this.title = projectEntity.getTitle();
        this.titleImg = new ImgDto(projectEntity.getTitleImg());
        this.completionRate = completionRate!=null ?  new BigDecimal(completionRate).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0;
        this.likeCount = likeCount;
        this.createdAt = projectEntity.getCreatedAt();
        this.userCount = userCount;
        this.isLiked = isLiked != 0L;
        this.expiredAt = projectEntity.getExpiredAt();
        this.startAt = projectEntity.getStartAt();
        this.viewCount = viewCount;
        // this.totalCount = totalCount;
        this.projectViewCreateAt = projectViewCreateAt;
        this.status = projectEntity.getProjectStatus().name();

        author = UserSummeryInfoDto.of(projectEntity.getUser());

        this.projectEntity = projectEntity;
    }

    public void updateTag(ProjectEntity project) {
        this.projectEntity.setTags(project.getTags());
        tags = project.getTags().stream().map(ProjectTagEntity::getTag).map(TagEntity::getName).toList();
    }

}