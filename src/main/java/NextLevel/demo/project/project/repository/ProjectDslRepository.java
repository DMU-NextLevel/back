package NextLevel.demo.project.project.repository;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.dto.request.RequestMainPageProjectListDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import NextLevel.demo.project.tag.entity.QProjectTagEntity;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.user.entity.QLikeEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectDslRepository {

    private final SelectProjectListDslRepository selectProjectRepository;
    private final FundingDslRepository fundingDslRepository;

    public ResponseProjectListDto selectProjectDsl(RequestMainPageProjectListDto dto) {
        QProjectTagEntity projectTagEntity = new QProjectTagEntity("projectTagEntity");
        SelectProjectListDslRepository.Builder builder = selectProjectRepository
                .builder(dto.getUserId());

        String search = dto.getSearch();
        if(search != null && !search.isEmpty())
            builder.where(QProjectEntity.class, (project)->whereSearch(project, dto.getSearch()));

        List<Long> tagIds = dto.getTagIds();
        if(tagIds != null && !tagIds.isEmpty()) {
            builder.leftJoin(projectTagEntity, QProjectEntity.class, (project)->projectTagEntity.project.id.eq(project.id));
            builder.where(null, (entity) -> projectTagEntity.tag.id.in(dto.getTagIds()));
        }

        builder.where(QProjectEntity.class, (projectEntity) ->
                {
                    if(dto.getStatus() == null || dto.getStatus().isEmpty())
                        return projectEntity.projectStatus.in(ProjectStatus.PROGRESS, ProjectStatus.STOPPED);
                    else
                        return projectEntity.projectStatus.in(dto.getStatus());
                }
        );

        if(dto.getOrder() != null && !dto.getOrder().isEmpty() && dto.getDesc() != null)
            orderByType(builder, ProjectOrderType.getType(dto.getOrder()), dto.getDesc());

        ResponseProjectListDto projectList = builder
                .limit(dto.getLimit(), dto.getPage())
                .commit();

        return projectList;
    }

    private BooleanExpression whereSearch(QProjectEntity projectEntity, String search) {
        if(search != null && !search.isEmpty()) {
            return projectEntity.title.like("%"+search+"%");
        }
        return Expressions.TRUE;
    }

    private void orderByType(SelectProjectListDslRepository.Builder builder, ProjectOrderType type, Boolean desc) {
        switch(type) {
            case ProjectOrderType.CREATED:
                builder.orderBy(QProjectEntity.class, (project)-> desc?project.createdAt.desc():project.createdAt.asc());
                return;
            case ProjectOrderType.RECOMMEND:
                builder.orderBy(QLikeEntity.class, (like)->desc?like.count().desc():like.count().asc());
                return;
            case ProjectOrderType.EXPIRED:
                builder.orderBy(QProjectEntity.class, (project)->desc?project.expiredAt.desc():project.expiredAt.asc());
                return;
            case ProjectOrderType.USER: // 펀딩 수 -> sub query 사용
                builder.orderBy(QProjectEntity.class, (project)->
                        desc?
                                Expressions.asNumber(fundingDslRepository.fundingUserCount(project)).desc():
                                Expressions.asNumber(fundingDslRepository.fundingUserCount(project)).asc()
                );
                return;
            case ProjectOrderType.VIEW:
                builder.orderBy(QProjectViewEntity.class, (view)->desc?view.count().desc():view.count().asc());
                return;
            case ProjectOrderType.COMPLETION:
                builder.orderBy(QProjectEntity.class, (project)->
                        desc?
                            Expressions.asNumber(fundingDslRepository.completeRate(project)).desc():
                                Expressions.asNumber(fundingDslRepository.completeRate(project)).asc()
                );
                return;
        }
    }

}
