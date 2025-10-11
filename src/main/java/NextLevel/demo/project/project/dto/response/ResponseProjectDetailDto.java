package NextLevel.demo.project.project.dto.response;

import NextLevel.demo.funding.FundingUtil;
import NextLevel.demo.img.ImgDto;
import NextLevel.demo.project.project.entity.ProjectEntity;

import java.time.LocalDate;
import java.util.List;

import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.tag.entity.TagEntity;
import NextLevel.demo.user.dto.user.response.UserSocialProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProjectDetailDto {
    private Long id;
    private String title;
    private String content;

    private ImgDto titleImg;

    private UserSocialProfileDto user;

    private List<String> tag;

    private LocalDate createdAt;
    private LocalDate startAt;
    private LocalDate expiredAt;
    private String status;

    private Boolean isAuthor;

    private Long goal;
    private Long sum; // 현재 모인 금액의 총액
    private Double completionRate;

    private int likeCount;
    private long fundingCount;
    private Long viewCount; // 조회수

    @JsonProperty("isLike")
    private boolean isLike;

    public static ResponseProjectDetailDto of(
            ProjectEntity entity,
            Long fundingPrice,
            Long fundingCount,
            Long userId,
            Long likeCount,
            Long isLike,
            Long viewCount,
            UserSocialProfileDto userSocialProfileDto
    ) {
        ResponseProjectDetailDto dto = new ResponseProjectDetailDto();
        dto.id = entity.getId();
        dto.title = entity.getTitle();
        dto.content = entity.getContent();
        dto.titleImg = new ImgDto(entity.getTitleImg());
        dto.createdAt = entity.getCreatedAt().toLocalDate();
        dto.goal = entity.getGoal();
        dto.sum = fundingPrice;
        dto.completionRate = FundingUtil.getCompletionRate(dto.sum, dto.goal);
        dto.likeCount = likeCount!=null?likeCount.intValue():0;
        dto.isAuthor = entity.getUser().getId().equals(userId);
        dto.status = entity.getProjectStatus().name();
        dto.startAt = entity.getStartAt();
        dto.expiredAt = entity.getExpiredAt();
        dto.setFundingCount(fundingCount); // 펀딩의 총 갯수

        dto.viewCount = viewCount !=null ? viewCount : 0L; // 조회한 조회 수

        dto.isLike = isLike!=null?isLike.equals(1L):false;
        dto.user = userSocialProfileDto;

        dto.tag = entity.getTags().stream().map(ProjectTagEntity::getTag).map(TagEntity::getName).toList();

        return dto;
    }

}
