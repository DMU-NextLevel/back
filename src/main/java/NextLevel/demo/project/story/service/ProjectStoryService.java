package NextLevel.demo.project.story.service;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.story.repository.ProjectStoryRepository;
import java.nio.file.Path;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectStoryService {

    private final ProjectStoryRepository projectStoryRepository;
    private final ImgServiceImpl imgService;
    private final ProjectValidateService projectValidateService;

    @ImgTransaction
    @Transactional
    public void saveNewProjectStory(ProjectEntity project, List<MultipartFile> imgFiles, ImgPath imgPath) {
        imgFiles.forEach(imgFile -> {
            projectStoryRepository.save(
                    ProjectStoryEntity
                            .builder()
                            .project(project)
                            .img(imgService.saveImg(imgFile, imgPath))
                            .build()
            );
        });
    }

    @Transactional
    @ImgTransaction
    public void updateProjectStory(ProjectEntity project, List<MultipartFile> imgFiles, ImgPath imgPath) {
        List<ImgEntity> oldImgs = project.getStories().stream().map(projectImg -> projectImg.getImg()).toList();

        projectStoryRepository.deleteAllByProjectId(project.getId());

        oldImgs.forEach(img->imgService.deleteImg(img, imgPath));
        saveNewProjectStory(project, imgFiles, imgPath);
    }

    @ImgTransaction
    @Transactional
    public void updateProjectStory(Long projectId, Long userId, List<MultipartFile> imgFiles, ImgPath imgPath){
        if(imgFiles == null || imgFiles.isEmpty())
            return;

        ProjectEntity project = projectValidateService.validateAuthor(projectId, userId);

        updateProjectStory(project, imgFiles, imgPath);
    }

    public List<ProjectStoryEntity> getProjectStory(Long projectId) {
        projectValidateService.getProjectEntity(projectId);
        return projectStoryRepository.findAllByProjectOrderByCreatedAt(projectId);
    }
}
