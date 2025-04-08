package NextLevel.demo.project.repository;

import NextLevel.demo.project.ProjectOrderType;
import NextLevel.demo.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.project.entity.ProjectEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectActivityRepository {

    private final EntityManager entityManager;

    @Value("${page_count}")
    private int pageCount;

    // tag 값이 있느면 where 추가
    // order by type에 맞게 order
    @Transactional
    public List<ResponseProjectListDto> getAll(Long tagId, ProjectOrderType orderType, Integer page) {
        String select = """ 
            WITH filtered_projects AS (
                SELECT p.*
                FROM project p
                JOIN project_tag pt ON p.id = pt.project_id
                %s
            ),
            project_count AS (
                SELECT COUNT(*) AS total_count
                FROM filtered_projects
            )
            SELECT fp.id, fp.title, fp.user_id, fp.created_at,\s
                (CAST((SELECT SUM(f.price) FROM funding f WHERE f.project_id = fp.id) AS DOUBLE) * 100.0 / fp.goal) AS completion_rate,
                (SELECT COUNT(DISTINCT r.user_id) FROM recommend r WHERE r.project_id = fp.id) AS recommend_count,
                (SELECT COUNT(f.user_id) FROM funding f WHERE f.project_id = fp.id) AS user_count,
                (SELECT i.uri FROM img i WHERE i.id = fp.img_id) AS title_img,
                pc.total_count
            FROM filtered_projects fp
            CROSS JOIN project_count pc
            %s 
            %s
            """;
        String where = "";
        String order = "";
        String limit = "";

        if(tagId != null) {
            where = " where pt.tag_id = "+ tagId + " ";
        }

        if(orderType == null)
            orderType = ProjectOrderType.RECOMMEND;

        if(page == null)
            page = 1;

        limit = " limit " + pageCount + " offset " + (page-1) * pageCount + " ";

        Query query = entityManager.createNativeQuery(String.format(select, where, order, limit), ResponseProjectListDto.class);

        List<ResponseProjectListDto> dtos = query.getResultList();
        return dtos;
    }
}
