package NextLevel.demo.user.service;

import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.user.dto.LikeDto;
import NextLevel.demo.user.entity.LikeEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.LikeRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final EntityManager entityManager;
    private final LikeRepository likeRepository;
    private final ProjectValidateService projectValidateService;

    @Transactional
    public void like(LikeDto dto) {
        ProjectEntity targetProject = projectValidateService.getProjectEntity(dto.getProjectId());
        Optional<LikeEntity> oldLikeOpt = likeRepository.findByUser_IdAndProject_Id(dto.getUserId(), dto.getProjectId());
        if(dto.getLike() && oldLikeOpt.isEmpty()) {
            // 좋야요를 누름
            likeRepository.save(LikeEntity
                .builder()
                .user(entityManager.getReference(UserEntity.class, dto.getUserId()))
                .project(targetProject)
                .build());
        }
        if(!dto.getLike() && oldLikeOpt.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(oldLikeOpt.get());
        }

    }
}
