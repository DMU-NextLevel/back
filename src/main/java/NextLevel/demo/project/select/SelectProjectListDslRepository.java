package NextLevel.demo.project.select;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.tag.entity.QTagEntity;
import NextLevel.demo.project.tag.entity.TagEntity;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.project.tag.entity.QProjectTagEntity;
import NextLevel.demo.user.entity.QLikeEntity;
import NextLevel.demo.user.entity.QUserEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SelectProjectListDslRepository {
    private final JPAQueryFactory queryFactory;
    private final FundingDslRepository fundingDslRepository;

    private QProjectEntity projectEntity = new QProjectEntity("default_project");

    public Builder builder(Long userId) { return new Builder(queryFactory, userId); }

    public class Builder {
        private JPAQuery<ResponseProjectListDetailDto> mainQuery;
        private JPAQuery<Long> totalCountQuery;
        private BooleanExpression where = Expressions.TRUE;
        private List<OrderSpecifier> orderBy = new ArrayList<>();
        private long limit;
        private long page;
        private Long userId;

        private QUserEntity userEntity = new QUserEntity("default_user");
        private QLikeEntity likeEntity = new QLikeEntity("default_like");
        private QLikeEntity isLikeEntity = new QLikeEntity("default_is_like");
        private QProjectViewEntity viewEntity = new QProjectViewEntity("default_view");

        public Builder(JPAQueryFactory queryFactory, Long userId) {
            this.userId = userId;
            totalCountQuery = queryFactory.selectOne()
                    .select(projectEntity.countDistinct())
                    .from(projectEntity)
                    .leftJoin(likeEntity).on(likeEntity.project.id.eq(projectEntity.id))
                    .leftJoin(isLikeEntity).on(likeEntity.project.id.eq(projectEntity.id).and(userId!=null?isLikeEntity.user.id.eq(userId):Expressions.FALSE))
                    .leftJoin(viewEntity).on(viewEntity.project.id.eq(projectEntity.id));

            mainQuery = queryFactory
                    .select(Projections.constructor(ResponseProjectListDetailDto.class,
                        projectEntity,
                        // completeRate
                        fundingDslRepository.completeRate(projectEntity),
                        // like count
                        likeEntity.countDistinct(),
                        // user_count
                        fundingDslRepository.fundingUserCount(projectEntity),
                        // is_like
                        isLikeEntity.countDistinct(),
                        // view_count
                        viewEntity.countDistinct()
                    ))
                    .from(projectEntity)
                    .leftJoin(projectEntity.user, userEntity).fetchJoin()
                    .leftJoin(projectEntity.titleImg).fetchJoin()
                    .leftJoin(likeEntity).on(likeEntity.project.id.eq(projectEntity.id))
                    .leftJoin(isLikeEntity).on(isLikeEntity.project.id.eq(projectEntity.id).and(userId!=null?isLikeEntity.user.id.eq(userId):Expressions.FALSE))
                    .leftJoin(viewEntity).on(viewEntity.project.id.eq(projectEntity.id));
        }
        public <T extends EntityPathBase,J extends EntityPathBase> Builder leftJoin(
                T joinEntity,
                Class<J> entityClass, FunctionInterface<BooleanExpression, J> onFunction
        ){
            J entity = (J) getEntity(entityClass);

            mainQuery.leftJoin(joinEntity).on(onFunction.function(entity));
            totalCountQuery.leftJoin(joinEntity).on(onFunction.function(entity));
            return this;
        }
        public <T extends EntityPathBase> Builder where(Class<T> entityClass, FunctionInterface<BooleanExpression, T> whereFunction) {
            where = where.and(whereFunction.function(getEntity(entityClass))); return this;
        }
        public <T extends EntityPathBase> Builder orderBy(Class<T> entityClass, FunctionInterface<OrderSpecifier, T> whereFunction) {
            orderBy.add(whereFunction.function(getEntity(entityClass))); return this;
        }
        public void whereIsLike() {
            where = where.and(isLikeEntity.isNotNull());
        }
        public Builder limit(long limit, long page) {
            this.limit = limit; this.page = page; return this;
        }
        public ResponseProjectListDto commit(){
            orderBy.add(projectEntity.createdAt.desc());
            //List<ResponseProjectListDetailDto> projectList = selectProjectsWithSingle(userId, where, orderBy.toArray(OrderSpecifier[]::new), limit, page * limit);
            List<ResponseProjectListDetailDto> projectList =
                    mainQuery
                            .where(where)
                            .groupBy(projectEntity)
                            .orderBy(orderBy.toArray(OrderSpecifier[]::new))
                            .limit(limit)
                            .offset(page * limit)
                            .fetch();

            setTag(projectList);

            return new ResponseProjectListDto(projectList, totalCountQuery.where(where).fetchOne(), limit, page);
        }

        private <T extends EntityPathBase> T getEntity(Class<T> entityClass) {
            if(entityClass == null)
                return null;
            if(entityClass.equals(QProjectViewEntity.class))
                return (T) viewEntity;
            if(entityClass.equals(QLikeEntity.class))
                return (T) likeEntity;
            if(entityClass.equals(QProjectEntity.class))
                return (T) projectEntity;
            else
                return null;
        }
    }
//
//    private List<ResponseProjectListDetailDto> selectProjectsWithSingle (
//            Long userId,
//            BooleanExpression where,
//            OrderSpecifier[] orderBy,
//            long limit, long offset
//    ) {
//        JPAQuery<ResponseProjectListDetailDto> ob = queryFactory
//                .selectDistinct(Projections.constructor(ResponseProjectListDetailDto.class,
//                        projectEntity,
//
//                        // completeRate
//                        fundingDslRepository.completeRate(projectEntity),
//
//                        // like count
//                        likeCount(projectEntity),
//
//                        // user_count
//                        fundingDslRepository.fundingUserCount(projectEntity),
//
//                        // is_like
//                        isLike(projectEntity, userId),
//
//                        // view_count
//                        viewCount(projectEntity),
//
//                        projectViewEntity.createAt // select distinct 용 컬럼
//                ))
//                .from(projectEntity); // 그냥 여기서 return 을 때리고 나머지를 다른 부분에서 구현한다면? 아님 진짜 불리를 하는게 맞는 건가?
//
//
//
//                .leftJoin(likeEntity).on(projectEntity.id.eq(likeEntity.project.id)).fetchJoin()
//                .leftJoin(projectViewEntity).on(latestProjectViewOn(projectEntity, projectViewEntity, userId)).fetchJoin()
//                .where(where)
//                .orderBy(orderBy)
//                .limit(limit) //(dto.getPageCount())
//                .offset(offset) // (dto.getPageCount() * dto.getPage())
//                .fetch();
//    }
//
//    private BooleanExpression latestProjectViewOn(
//            QProjectEntity project,
//            QProjectViewEntity projectView, Long userId
//    ) {
//        QProjectViewEntity subView = new QProjectViewEntity("subView");
//
//        if(userId == null)
//            return Expressions.FALSE;
//
//        return projectView.id.eq(
//                JPAExpressions
//                        .select(subView.id.max())
//                        .from(subView)
//                        .where(subView.project.id.eq(project.id).and(subView.user.id.eq(userId)))
//                        .groupBy(subView.user.id)
//        );
//    }


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
        Map<Long, ProjectEntity> tagMap = new HashMap<>();
        List<Long> projectIds = projectList.stream().map(projectListDto->projectListDto.getId()).toList();
        QProjectEntity project = new QProjectEntity("projectEntityForTag");
        QProjectTagEntity projectTag = new QProjectTagEntity("projectTagEntityForTag");
        QTagEntity tag = new QTagEntity("tagEntityForTag");

        List<ProjectEntity> projectListWithTag = queryFactory
                .select(project)
                .from(project)
                //.leftJoin(projectTag).on(project.id.eq(projectTag.project.id)).fetchJoin()
                .leftJoin(project.tags, projectTag).fetchJoin()
                //.leftJoin(tag).on(tag.id.eq(projectTag.tag.id)).fetchJoin()
                .leftJoin(projectTag.tag, tag).fetchJoin()
                .where(project.id.in(projectIds))
                .fetch();

        projectListWithTag.stream().forEach(projectEntity -> tagMap.put(projectEntity.getId(), projectEntity));

        projectList.forEach(projectListDto ->
                projectListDto.updateTag(tagMap.get(projectListDto.getProjectEntity().getId()))
        );
        return projectList;
    }

//    public Expression<Long> isLike(QProjectEntity projectEntity, Long userId) {
//        if(userId == null)
//            return Expressions.constant(0L);
//
//        return JPAExpressions
//            .select(likeEntity.user.id)
//            .from(likeEntity)
//            .where(likeEntity.project.id.eq(projectEntity.id).and(likeEntity.user.id.eq(userId)));
//    }
//
//    public Expression<Long> viewCount(QProjectEntity projectEntity) {
//        return JPAExpressions
//            .select(projectViewEntity.count())
//            .from(projectViewEntity)
//            .where(projectViewEntity.project.id.eq(projectEntity.id));
//    }
//
//
//    public Expression<Long> likeCount(QProjectEntity projectEntity) {
//        return JPAExpressions
//            .select(likeEntity.count())
//            .from(likeEntity)
//            .where(likeEntity.project.id.eq(projectEntity.id));
//    }

}
