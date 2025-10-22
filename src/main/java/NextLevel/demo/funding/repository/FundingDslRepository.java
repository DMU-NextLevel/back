package NextLevel.demo.funding.repository;

import static NextLevel.demo.funding.entity.QFreeFundingEntity.freeFundingEntity;
import static NextLevel.demo.funding.entity.QOptionFundingEntity.optionFundingEntity;
import static NextLevel.demo.project.project.entity.QProjectEntity.projectEntity;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.entity.QCouponEntity;
import NextLevel.demo.funding.entity.QFreeFundingEntity;
import NextLevel.demo.funding.entity.QOptionFundingEntity;
import NextLevel.demo.option.QOptionEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.entity.QProjectEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FundingDslRepository {

    private final JPAQueryFactory queryFactory;

    public boolean isFreeFunding(Long projectId, Long userId) {
        return queryFactory
                .select(isFreeFunding(projectEntity, userId))
                .from(projectEntity)
                .where(projectEntity.id.eq(projectId))
                .fetchOne();
    }

    public void addFundingData(List<ProjectEntity> projectList, Long userId) {
        QProjectEntity project = new QProjectEntity("project");
        QOptionEntity option = new QOptionEntity("option");
        QOptionFundingEntity optionFunding = new QOptionFundingEntity("optionFunding");
        QFreeFundingEntity freeFunding = new QFreeFundingEntity("freeFunding");
        QCouponEntity coupon = new QCouponEntity("coupon");

        BooleanExpression where = project.id.in(projectList.stream().map(ProjectEntity::getId).toList());

        List<ProjectEntity> projectListWithOptionFunding = queryFactory
                .select(project)
                .from(project)
                .leftJoin(project.options, option).fetchJoin()
                .leftJoin(option.fundings, optionFunding).fetchJoin()
                .leftJoin(optionFunding.coupon).fetchJoin()
                .where(where.and(userId!=null?optionFunding.user.id.eq(userId):Expressions.TRUE))
                .fetch();

        List<ProjectEntity> projectListWithFreeFunding = queryFactory
                .select(project)
                .from(project)
                .leftJoin(project.freeFundings, freeFunding).fetchJoin()
                .where(where.and(userId!=null?freeFunding.user.id.eq(userId):Expressions.TRUE))
                .fetch();

        Map<Long, ProjectEntity> result = new HashMap<>();
        projectListWithOptionFunding.forEach(p->result.put(p.getId(), p));
        projectListWithFreeFunding.forEach(p->result.put(p.getId(), p));

        projectList.forEach(p->p.setFundingData(result.get(p.getId())));
    }

    public Expression<Double> completeRate(QProjectEntity projectEntity) {
        QProjectEntity newProjectEntity = new QProjectEntity("projectEntity3");
        return queryFactory
                .select(
                        optionFundingEntity.option.price.longValue().multiply(optionFundingEntity.count).sum().coalesce(0L)
                                .add(freeFundingEntity.price.longValue().sum().coalesce(0L))
                                .doubleValue()
                                .divide(projectEntity.goal)
                                .multiply(100)
                )
                .from(newProjectEntity)
                .leftJoin(optionFundingEntity).on(optionFundingEntity.option.project.id.eq(projectEntity.id))
                .leftJoin(freeFundingEntity).on(freeFundingEntity.project.id.eq(projectEntity.id))
                .where(newProjectEntity.id.eq(projectEntity.id));
    }

    public Expression<Long> fundingUserCount(QProjectEntity projectEntity) {
        QProjectEntity newProjectEntity = new QProjectEntity("projectEntity2");
        return JPAExpressions
                .select(optionFundingEntity.count.sum().add(freeFundingEntity.count()))
                .from(newProjectEntity)
                .leftJoin(optionFundingEntity).on(optionFundingEntity.option.project.id.eq(projectEntity.id))
                .leftJoin(freeFundingEntity).on(freeFundingEntity.project.id.eq(projectEntity.id))
                .where(newProjectEntity.id.eq(projectEntity.id));
    }


    // select from funding where f.project = :id and f.user = :id
    public BooleanExpression isFunding(QProjectEntity project, Long userId) {
        return isFreeFunding(project, userId).or(isOptionFunding(project, userId));
    }

    public BooleanExpression isFreeFunding(QProjectEntity project, Long userId) {
        if(userId == null)
            return Expressions.FALSE;
        return queryFactory
                .select()
                .from(freeFundingEntity)
                .where(freeFundingEntity.project.id.eq(project.id).and(freeFundingEntity.user.id.eq(userId)))
                .exists();
    }

    public BooleanExpression isOptionFunding(QProjectEntity project, Long userId) {
        if(userId == null)
            return Expressions.FALSE;
        return queryFactory
                .select(optionFundingEntity)
                .from(optionFundingEntity)
                .where(optionFundingEntity.option.project.id.eq(project.id).and(optionFundingEntity.user.id.eq(userId)))
                .exists();
    }

}
