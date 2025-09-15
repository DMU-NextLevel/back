package NextLevel.demo.project.story.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgServiceImpl;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.entity.ProjectEntity;
import NextLevel.demo.project.project.repository.ProjectRepository;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.story.repository.ProjectStoryRepository;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectStoryService {

    private final ProjectRepository projectRepository;
    private final ProjectStoryRepository projectStoryRepository;
    private final ImgServiceImpl imgService;

    @ImgTransaction
    @Transactional
    public void saveNewProjectStory(ProjectEntity project, List<MultipartFile> imgFiles, ArrayList<Path> imgPaths) {
        imgFiles.forEach(imgFile -> {
            projectStoryRepository.save(
                    ProjectStoryEntity
                            .builder()
                            .project(project)
                            .img(imgService.saveImg(imgFile, imgPaths))
                            .build()
            );
        });
    }

    @Transactional
    @ImgTransaction
    public void updateProjectStory(ProjectEntity project, List<MultipartFile> imgFiles, ArrayList<Path> imgPaths) {
        List<ImgEntity> oldImgs = project.getStories().stream().map(projectImg -> projectImg.getImg()).toList();

        projectStoryRepository.deleteAllByProjectId(project.getId());

        oldImgs.forEach(img->imgService.deleteImg(img));
        saveNewProjectStory(project, imgFiles, imgPaths);
    }

    @ImgTransaction
    @Transactional
    public void updateProjectStory(Long projectId, Long userId, List<MultipartFile> imgFiles, ArrayList<Path> imgPaths){
        ProjectEntity oldProject = projectRepository.findByIdWithAll(projectId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND, "project")
        );
        if(oldProject.getUser().getId() != userId)
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        updateProjectStory(oldProject, imgFiles, imgPaths);
    }
}
