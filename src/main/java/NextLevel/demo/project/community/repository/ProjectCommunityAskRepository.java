package NextLevel.demo.project.community.repository;

import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectCommunityAskRepository extends JpaRepository<ProjectCommunityAskEntity, Long> {

    @Query("select ask from ProjectCommunityAskEntity ask left join fetch ask.answer where ask.project.id = :projectId")
    List<ProjectCommunityAskEntity> findAllWithAnswerByProjectId(@Param("projectId") Long projectId);

}
