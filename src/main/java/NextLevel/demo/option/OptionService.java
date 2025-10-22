package NextLevel.demo.option;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.service.FundingRollbackService;
import NextLevel.demo.project.project.entity.ProjectEntity;

import java.util.List;

import NextLevel.demo.project.project.service.ProjectValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProjectValidateService projectValidateService;
    private final FundingRollbackService fundingRollbackService;

    public void add(SaveOptionRequestDto dto){
        ProjectEntity project = projectValidateService.validateAuthor(dto.getProjectId(), dto.getUserId());

        optionRepository.save(dto.toEntity(project));
    }

    @Transactional
    public void update(SaveOptionRequestDto dto){
        OptionEntity option = optionRepository.findById(dto.getOptionId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "option");}
        );
        projectValidateService.validateAuthor(option.getProject().getId(), dto.getUserId());
        fundingRollbackService.rollbackByOption(option);
        option.update(dto);
    }

    @Transactional
    public void delete(Long optionId, Long userId){
        OptionEntity option = optionRepository.findById(optionId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "option");}
        );
        projectValidateService.validateAuthor(option.getProject().getId(), userId);
        fundingRollbackService.rollbackByOption(option);
        optionRepository.deleteById(option.getId());
    }

    public List<OptionDto> getAllOptions(Long projectId) {
        List<OptionEntity> options = optionRepository.findByProjectId(projectId);
        return options.stream().map(OptionDto::of).toList();
    }

}
