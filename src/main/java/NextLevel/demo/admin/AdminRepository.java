package NextLevel.demo.admin;

import NextLevel.demo.admin.project.FundingDataDao;
import NextLevel.demo.user.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface AdminRepository extends Repository<UserEntity, Long> {

    @Modifying
    @Query(value = """
        select
           project.title as project_title ,
           t1.price as price ,
           user.nick_name as user_nick_name ,
           t1.created_at as created_at ,
           t1.type as type
         from
          (
            select
              `option`.project_id as project_id ,
              `option`.price * off.count as price ,
              off.user_id as user_id ,
              off.created_at as created_at ,
              "option" as type
            from option_funding as off
            left join `option` on off.option_id = `option`.id
         union all
            select
              ff.project_id as project_id ,
              ff.price as price ,
              ff.user_id as user_id ,
              ff.created_at as created_at ,
              "free" as type
            from free_funding as ff
          ) as t1
         left join project on t1.project_id = project.id
         left join user on t1.user_id = user.id
          limit :limit
          offset :offset
         ;
    """, nativeQuery = true)
    List<FundingDataDao> selectAllFunding(@Param("limit") Long limit, @Param("offset") Long offset);

}
