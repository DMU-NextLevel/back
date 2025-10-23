package NextLevel.demo.project.notice.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.notice.dto.request.SaveProjectNoticeRequestDto;
import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import NextLevel.demo.project.notice.repository.ProjectNoticeRepository;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectNoticeService {

    private final ProjectNoticeRepository projectNoticeRepository;
    private final ImgServiceImpl imgService;
    private final ProjectValidateService projectValidateService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProjectNoticeEntity> getAllNotice(Long projectId){
        ProjectEntity project = projectValidateService.getProjectEntity(projectId);
        return projectNoticeRepository.findAllByProjectOrderByCreatedAt(project.getId());
    }

    @Transactional
    @ImgTransaction
    public void saveProjectNotice(SaveProjectNoticeRequestDto dto, ImgPath imgPath) {
        ProjectEntity project = projectValidateService.getProjectEntity(dto.getProjectId());

        if(!project.getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        ImgEntity savedImg = imgService.saveImg(dto.getImg(), imgPath);

        projectNoticeRepository.save(dto.toEntity(savedImg, project));
    }

    @Transactional
    @ImgTransaction
    public void updateNotice(SaveProjectNoticeRequestDto dto,  ImgPath imgPath) {
        ProjectNoticeEntity notice = projectNoticeRepository.findByIdWithProject(dto.getNoticeId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );

        if(!notice.getProject().getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        notice.update(dto);

        if(dto.getImg() != null && !dto.getImg().isEmpty()) {
            ImgEntity savedImg = imgService.updateImg(dto.getImg(), notice.getImg(), imgPath);
            if(notice.getImg() == null)
                notice.setImg(savedImg);
        }
    }

    @Transactional
    @ImgTransaction
    public void deleteProjectNotice(Long id, Long userId, ImgPath imgPath) {
        ProjectNoticeEntity notice = projectNoticeRepository.findByIdWithProject(id).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );
        projectValidateService.validateAuthor(notice.getProject(), userId);

        ImgEntity img = notice.getImg();
        projectNoticeRepository.deleteById(id);

        imgService.deleteImg(img, imgPath);
    }

    @Transactional
    @ImgTransaction
    public void deleteAllProjectNotice(Long projectId, ImgPath imgPath) {
        ProjectEntity project = projectValidateService.getProjectEntity(projectId);

        List<ProjectNoticeEntity> noticeList = projectNoticeRepository.findAllByProjectId(project.getId());

        List<ImgEntity> imgList = noticeList.stream().map(ProjectNoticeEntity::getImg).toList();

        projectNoticeRepository.deleteAll(noticeList);

        entityManager.flush();

        imgList.forEach(img -> imgService.deleteImg(img, imgPath));
    }

}
