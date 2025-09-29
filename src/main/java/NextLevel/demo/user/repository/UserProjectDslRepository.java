package NextLevel.demo.user.repository;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.entity.QLikeEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserProjectDslRepository {

    private final SelectProjectListDslRepository selectProjectRepository;
    private final FundingDslRepository fundingDslRepository;

    private QProjectViewEntity projectViewEntity = new QProjectViewEntity("viewEntity");

    public ResponseProjectListDto myProject(RequestMyPageProjectListDto dto) {
        SelectProjectListDslRepository.Builder builder = selectProjectRepository.builder();
        builder = where(builder, dto.getUserId(), dto.getType());
        builder = where(builder, dto.getProjectStatus());
        return builder
                .limit(dto.getLimit(), dto.getPage())
                .commit(dto.getUserId());
    }

    private SelectProjectListDslRepository.Builder where(SelectProjectListDslRepository.Builder builder, ProjectStatus projectStatus) {
        if(projectStatus == null)
            return builder;
        return builder.where(QProjectEntity.class, (project)->project.projectStatus.eq(projectStatus));
    }

    private SelectProjectListDslRepository.Builder where(SelectProjectListDslRepository.Builder builder, Long userId, MyPageProjectListType type) {
        switch(type) {
            case MyPageProjectListType.PROJECT:
                builder.where(QProjectEntity.class, (project)->project.user.id.eq(userId));
                break;
            case MyPageProjectListType.FUNDING:
                builder.where(QProjectEntity.class, (project)->fundingDslRepository.isFunding(project, userId));
                break;
            case MyPageProjectListType.LIKE:
                builder.where(QLikeEntity.class, (like)->like.user.id.eq(userId));
                break;
            case MyPageProjectListType.VIEW:
                builder.where(QProjectEntity.class, (project)->
                        findViewOne(project, userId).exists()
                );
                // builder.where(QProjectViewEntity.class, (view)->view.createAt.after(LocalDateTime.now().minusDays(10)));
//                builder.orderBy(QProjectViewEntity.class, (view)->
//                        projectViewEntity.createAt.desc() // 이건 되면 진짜
//                );
                break;
            default:
                throw new CustomException(ErrorCode.NOT_FOUND, "type");
        }
        return builder;
    }

    private JPQLQuery<Long> findViewOne(QProjectEntity project, Long userId) {
        return JPAExpressions
                .select(projectViewEntity.id.min())
                .from(projectViewEntity)
                .where(projectViewEntity.project.id.eq(project.id).and(projectViewEntity.user.id.eq(userId)));
    }

}
