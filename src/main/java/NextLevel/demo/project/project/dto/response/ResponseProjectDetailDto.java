package NextLevel.demo.project.project.dto.response;

import NextLevel.demo.funding.FundingUtil;
import NextLevel.demo.img.ImgDto;
import NextLevel.demo.project.project.entity.ProjectEntity;

import java.time.LocalDate;

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

    private LocalDate createdAt;
    private LocalDate startAt;
    private LocalDate expiredAt;
    private String status;

    private String authorNickName;

    private Boolean isAuthor;

    private Long goal;
    private Long sum; // 현재 모인 금액의 총액
    private Double completionRate;

    private int likeCount;
    private long fundingCount;
    private Long userCount;

    public static ResponseProjectDetailDto of(ProjectEntity entity, Long fundingPrice, Long fundingCount, Long userId) {
        ResponseProjectDetailDto dto = new ResponseProjectDetailDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setTitleImg(new ImgDto(entity.getTitleImg()));
        dto.setCreatedAt(entity.getCreatedAt().toLocalDate());
        dto.setAuthorNickName(entity.getUser().getNickName());
        dto.setGoal(entity.getGoal());
        dto.setSum(fundingPrice);
        dto.setCompletionRate(FundingUtil.getCompletionRate(dto.sum, dto.goal));
        dto.setLikeCount(entity.getLikes().size());
        dto.setIsAuthor(entity.getUser().getId() == userId);
        dto.status = entity.getProjectStatus().name();
        dto.startAt = entity.getStartAt();
        dto.expiredAt = entity.getExpiredAt();
        dto.setFundingCount(fundingCount); // 펀딩의 총 갯수

        dto.setUserCount(null); // 조회한 조회 수, 아직 추가 예정

        return dto;
    }

}
