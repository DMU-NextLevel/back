package NextLevel.demo.project.select;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.img.entity.QImgEntity;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.tag.entity.TagEntity;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.project.tag.entity.QProjectTagEntity;
import NextLevel.demo.user.entity.QLikeEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SelectProjectListDslRepository {
    private final JPAQueryFactory queryFactory;
    private final FundingDslRepository fundingDslRepository;

    private QProjectEntity projectEntity = QProjectEntity.projectEntity;
    private QProjectTagEntity projectTagEntity = QProjectTagEntity.projectTagEntity;
    private QLikeEntity likeEntity = QLikeEntity.likeEntity;
    private QProjectViewEntity projectViewEntity = QProjectViewEntity.projectViewEntity;
    private QImgEntity imgEntity = QImgEntity.imgEntity;

    public Builder builder() { return new Builder(); }

    public class Builder {
        private BooleanExpression where = Expressions.TRUE;
        private List<OrderSpecifier> orderBy = new ArrayList<>();
        private long limit;
        private long page;

        public <T extends EntityPathBase> Builder where(Class<T> entityClass, ProjectSelectFunctionInterface<BooleanExpression, T> whereFunction) {
            where = where.and(whereFunction.function(getEntity(entityClass))); return this;
        }
        public <T extends EntityPathBase> Builder orderBy(Class<T> entityClass, ProjectSelectFunctionInterface<OrderSpecifier, T> whereFunction) {
            orderBy.add(whereFunction.function(getEntity(entityClass))); return this;
        }
        public Builder limit(long limit, long page) {
            this.limit = limit; this.page = page; return this;
        }
        public ResponseProjectListDto commit(Long userId){
            orderBy.add(projectEntity.createdAt.desc());
            List<ResponseProjectListDetailDto> projectList = selectProjectsWithSingle(userId, where, orderBy.toArray(OrderSpecifier[]::new), limit, page * limit);
            projectList = setTag(projectList);
            return new ResponseProjectListDto(projectList, totalCount(where), limit, page);
        }

        private <T extends EntityPathBase> T getEntity(Class<T> entityClass) {
            if(entityClass.equals(QProjectTagEntity.class))
                return (T) projectTagEntity;
            if(entityClass.equals(QProjectViewEntity.class))
                return (T) projectViewEntity;
            if(entityClass.equals(QLikeEntity.class))
                return (T) likeEntity;
            else
                return (T) projectEntity;
        }
    }

    private List<ResponseProjectListDetailDto> selectProjectsWithSingle (
            Long userId,
            BooleanExpression where,
            OrderSpecifier[] orderBy,
            long limit, long offset
    ) {
        return queryFactory
                .selectDistinct(Projections.constructor(ResponseProjectListDetailDto.class,
                        projectEntity.id,
                        projectEntity.title,
                        projectEntity.titleImg,
                        projectEntity.createdAt,
                        projectEntity.expired,
                        projectEntity.goal,

                        // completeRate
                        fundingDslRepository.completeRate(projectEntity),

                        // like count
                        likeCount(projectEntity),

                        // user_count
                        fundingDslRepository.fundingUserCount(projectEntity),

                        // is_like
                        isLike(projectEntity, userId),

                        // view_count
                        viewCount(projectEntity),

                        projectViewEntity.createAt // select distinct 용 컬럼
                ))
                .from(projectEntity)
                // .leftJoin(imgEntity).on(projectEntity.titleImg.id.eq(imgEntity.id)).fetchJoin()
                // .leftJoin(projectTagEntity).on(projectEntity.id.eq(projectTagEntity.project.id))
                .leftJoin(likeEntity).on(projectEntity.id.eq(likeEntity.project.id))
                .leftJoin(projectViewEntity).on(latestProjectViewOn(projectEntity, projectViewEntity, userId))
                .where(where)
                .orderBy(orderBy)
                .limit(limit) //(dto.getPageCount())
                .offset(offset) // (dto.getPageCount() * dto.getPage())
                .fetch();
    }

    private BooleanExpression latestProjectViewOn(
            QProjectEntity project,
            QProjectViewEntity projectView, Long userId
    ) {
        QProjectViewEntity subView = new QProjectViewEntity("subView");

        if(userId == null)
            return Expressions.FALSE;

        return projectView.id.eq(
                JPAExpressions
                        .select(subView.id.max())
                        .from(subView)
                        .where(subView.project.id.eq(project.id).and(subView.user.id.eq(userId)))
                        .groupBy(subView.user.id)
        );
    }


    private long totalCount(BooleanExpression where){
        return queryFactory
                .select(projectEntity.count())
                .from(projectEntity)
                // .leftJoin(projectTagEntity).on(projectEntity.id.eq(projectTagEntity.project.id))
                // .leftJoin(projectViewEntity).on(projectViewEntity.project.id.eq(projectEntity.id))
                // .leftJoin(projectViewEntity).on(latestProjectViewOn(projectEntity, projectViewEntity))
                .where(where)
                .fetchOne();
    }

    private List<ResponseProjectListDetailDto> setTag(List<ResponseProjectListDetailDto> projectList){
        Map<Long, ProjectEntity> tagMap = new HashMap<Long, ProjectEntity>();
        List<ProjectEntity> projectListWithTag = queryFactory
                .select(projectEntity)
                .from(projectEntity)
                .leftJoin(projectTagEntity).on(projectEntity.id.eq(projectTagEntity.project.id))
                .where(projectEntity.id.eq(projectEntity.id))
                .fetch();
        projectListWithTag.stream().forEach(projectEntity -> tagMap.put(projectEntity.getId(), projectEntity));

        projectList.forEach(projectEntity ->
                projectEntity.setTags(tagMap.get(projectEntity.getId()).getTags().stream().map(ProjectTagEntity::getTag).map(TagEntity::getName).toList())
        );
        return projectList;
    }

    public Expression<Long> isLike(QProjectEntity projectEntity, Long userId) {
        if(userId == null)
            return Expressions.constant(0L);

        return JPAExpressions
            .select(likeEntity.user.id)
            .from(likeEntity)
            .where(likeEntity.project.id.eq(projectEntity.id).and(likeEntity.user.id.eq(userId)));
    }

    public Expression<Long> viewCount(QProjectEntity projectEntity) {
        return JPAExpressions
            .select(projectViewEntity.count())
            .from(projectViewEntity)
            .where(projectViewEntity.project.id.eq(projectEntity.id));
    }


    public Expression<Long> likeCount(QProjectEntity projectEntity) {
        return JPAExpressions
            .select(likeEntity.count())
            .from(likeEntity)
            .where(likeEntity.project.id.eq(projectEntity.id));
    }

}
