package NextLevel.demo.project.project.repository;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.project.dto.request.RequestMainPageProjectListDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.select.SelectProjectListDslRepository;
import NextLevel.demo.project.tag.entity.QProjectTagEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static NextLevel.demo.project.tag.entity.QProjectTagEntity.projectTagEntity;

@Repository
@RequiredArgsConstructor
public class ProjectDslRepository {

    private final SelectProjectListDslRepository selectProjectRepository;
    private final FundingDslRepository fundingDslRepository;

    public ResponseProjectListDto selectProjectDsl(RequestMainPageProjectListDto dto) {
        ResponseProjectListDto projectList = selectProjectRepository
                .builder()
                .where(QProjectEntity.class, (project)->whereSearch(project, dto.getSearch()))
                .where(QProjectEntity.class, (projectEntity)->whereTag(projectEntity, dto.getTagIds()))
                .orderBy(QProjectEntity.class, (project)->orderByType(project, ProjectOrderType.getType(dto.getOrder()), dto.getDesc()))
                .limit(dto.getLimit(), dto.getPage())
                .commit(dto.getUserId());

        return projectList;
    }

    private BooleanExpression whereSearch(QProjectEntity projectEntity, String search) {
        if(search != null && !search.isEmpty()) {
            return projectEntity.title.like("%"+search+"%");
        }
        return Expressions.TRUE;
    }

    private BooleanExpression whereTag(QProjectEntity projectEntity, List<Long> tagIds){
        if(tagIds != null && !tagIds.isEmpty() && tagIds.get(0) != null) {
            return JPAExpressions
                    .select(projectTagEntity.id)
                    .from(projectTagEntity)
                    .where(projectTagEntity.project.id.eq(projectEntity.id).and(projectTagEntity.tag.id.in(tagIds)))
                    .exists();
        }
        return Expressions.TRUE;
    }

    private OrderSpecifier<?> orderByType(QProjectEntity projectEntity, ProjectOrderType type, Boolean desc) {
        ComparableExpressionBase<?> order = projectEntity.createdAt;
        switch(type) {
            case ProjectOrderType.CREATED:
                order = projectEntity.createdAt;
                break;
            case ProjectOrderType.RECOMMEND:
                order = Expressions.asNumber(selectProjectRepository.likeCount(projectEntity));
                break;
            case ProjectOrderType.EXPIRED:
                order = projectEntity.expiredAt;
                break;
            case ProjectOrderType.USER:
                order = Expressions.asNumber(fundingDslRepository.fundingUserCount(projectEntity));
                break;
            case ProjectOrderType.VIEW:
                order = Expressions.asNumber(selectProjectRepository.viewCount(projectEntity));
                break;
            case ProjectOrderType.COMPLETION:
                order = Expressions.asNumber(fundingDslRepository.completeRate(projectEntity));
                break;
        }
        if(desc == null || desc)
            return order.desc();
        else
            return order.asc();
    }

}
