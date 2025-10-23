package NextLevel.demo.project.notice.repository;

import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectNoticeRepository extends JpaRepository<ProjectNoticeEntity, Long> {

    @Query("select n from ProjectNoticeEntity n left join fetch n.project where n.id = :noticeId")
    Optional<ProjectNoticeEntity> findByIdWithProject(@Param("noticeId") Long noticeId);

    @Query("select pn from ProjectNoticeEntity pn where pn.project.id = :projectId order by pn.createdAt desc")
    List<ProjectNoticeEntity> findAllByProjectOrderByCreatedAt(@Param("projectId") Long projectId);

    @Query("select n from ProjectNoticeEntity n where n.project.id = :projectId")
    List<ProjectNoticeEntity> findAllByProjectId(@Param("projectId") Long projectId);

}
