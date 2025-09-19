package NextLevel.demo.project.community.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.project.community.dto.request.SaveCommunityDto;
import NextLevel.demo.project.community.entity.ProjectCommunityAnswerEntity;
import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import NextLevel.demo.project.community.repository.ProjectCommunityAnswerRepository;
import NextLevel.demo.project.community.repository.ProjectCommunityAskRepository;
import NextLevel.demo.project.project.service.ProjectValidateService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectCommunityAnswerService {

    private final ProjectValidateService projectValidateService;
    private final UserValidateService userValidateService;

    private final ProjectCommunityAnswerRepository projectCommunityAnswerRepository;
    private final ProjectCommunityAskRepository projectCommunityAskRepository;

    @Transactional
    public void addAnswer(SaveCommunityDto dto) {
        Long askId = dto.getId();
        ProjectCommunityAskEntity ask = projectCommunityAskRepository.findById(askId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "community");}
        );
        UserEntity user = userValidateService.getUserInfo(dto.getUserId());

        projectValidateService.validateAuthor(ask.getProject(), user);

        if(ask.getAnswer() != null) {
            dto.setId(ask.getAnswer().getId());
            updateAnswer(dto);
            return;
        }

        projectCommunityAnswerRepository.save(dto.toAnswerEntity(user, ask));
    }

    @Transactional
    public void updateAnswer(SaveCommunityDto dto) {
        Long answerId = dto.getId();
        ProjectCommunityAnswerEntity answer = projectCommunityAnswerRepository.findById(answerId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "community");}
        );
        UserEntity user = userValidateService.getUserInfo(dto.getUserId());
        projectValidateService.validateAuthor(answer.getAsk().getProject(), user);
        answer.update(dto);
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        ProjectCommunityAnswerEntity answer = projectCommunityAnswerRepository.findById(answerId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "community");}
        );
        UserEntity user = userValidateService.getUserInfo(userId);
        projectValidateService.validateAuthor(answer.getAsk().getProject(), user);
        answer.getAsk().setNoAnswer();
        projectCommunityAnswerRepository.deleteById(answerId);
    }

}
