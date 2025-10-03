package NextLevel.demo.user.repository;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.entity.QFreeFundingEntity;
import NextLevel.demo.funding.entity.QOptionFundingEntity;
import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.option.QOptionEntity;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import NextLevel.demo.project.tag.entity.QProjectTagEntity;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.entity.QLikeEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserProjectDslRepository {

    private final SelectProjectListDslRepository selectProjectRepository;
    private final FundingDslRepository fundingDslRepository;

    private QProjectViewEntity view = new QProjectViewEntity("viewEntity");
    private QProjectTagEntity tag = new QProjectTagEntity("tagEntity");

    public ResponseProjectListDto myProject(RequestMyPageProjectListDto dto) {
        SelectProjectListDslRepository.Builder builder = selectProjectRepository.builder(dto.getUserId());
        where(builder, dto.getUserId(), dto.getType());
        where(builder, dto.getProjectStatus());

        List<Long> tagIds = dto.getTag();
        if(tagIds != null && !tagIds.isEmpty()){
            builder.leftJoin(tag, QProjectEntity.class, project->project.id.eq(tag.project.id), false);
            builder.where(null, entity->tag.tag.id.in(tagIds));
        }

        return builder
                .limit(dto.getLimit(), dto.getPage())
                .commit();
    }

    private void where(SelectProjectListDslRepository.Builder builder, ProjectStatus projectStatus) {
        if(projectStatus == null)
            return;
        builder.where(QProjectEntity.class, (project)->project.projectStatus.eq(projectStatus));
    }

    private void where(SelectProjectListDslRepository.Builder builder, Long userId, MyPageProjectListType type) {
        switch(type) {
            case MyPageProjectListType.PROJECT:
                builder.where(QProjectEntity.class, (project)->project.user.id.eq(userId));
                break;
            case MyPageProjectListType.FUNDING:
                builder.where(QProjectEntity.class, (project)->fundingDslRepository.isFunding(project, userId));
                break;
            case MyPageProjectListType.LIKE: // 내부 함수 처리
                builder.whereIsLike();
                break;
            case MyPageProjectListType.VIEW: // 새 view table left join 처리
                builder = builder.leftJoin(
                        view,
                        QProjectEntity.class,
                        (project)->view.user.id.eq(userId).and(project.id.eq(view.project.id)),
                        false
                );
                builder.where(null,entity->view.isNotNull());
                builder.orderBy(null, entity->view.createAt.max().desc());
                break;
            default:
                throw new CustomException(ErrorCode.NOT_FOUND, "type");
        }
    }

}
