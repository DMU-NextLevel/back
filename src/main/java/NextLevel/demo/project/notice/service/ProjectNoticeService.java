package NextLevel.demo.project.notice.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.notice.dto.request.SaveProjectNoticeRequestDto;
import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import NextLevel.demo.project.notice.repository.ProjectNoticeRepository;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import jakarta.persistence.EntityManager;
import java.nio.file.Path;
import java.util.ArrayList;
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

    @Transactional
    @ImgTransaction
    public void saveProjectNotice(SaveProjectNoticeRequestDto dto, ArrayList<Path> imgPaths) {
        ProjectEntity project = projectValidateService.getProjectEntity(dto.getProjectId());

        if(!project.getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        ImgEntity savedImg = imgService.saveImg(dto.getImg(), imgPaths);

        projectNoticeRepository.save(dto.toEntity(savedImg, project));
    }

    @Transactional
    @ImgTransaction
    public void updateNotice(SaveProjectNoticeRequestDto dto,  ArrayList<Path> imgPaths) {
        ProjectNoticeEntity notice = projectNoticeRepository.findByIdWithProject(dto.getNoticeId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );

        if(!notice.getProject().getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        notice.update(dto);

        if(dto.getImg() != null && !dto.getImg().isEmpty()) {
            ImgEntity savedImg = imgService.updateImg(dto.getImg(), notice.getImg(), imgPaths);
            if(notice.getImg() == null)
                notice.setImg(savedImg);
        }
    }

    public void deleteProjectNotice(Long id, Long userId) {
        ProjectNoticeEntity notice = projectNoticeRepository.findByIdWithProject(id).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );
        if(!notice.getProject().getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        projectNoticeRepository.deleteById(id);
    }

}
