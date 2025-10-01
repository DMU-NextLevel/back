package NextLevel.demo.user.service;

import NextLevel.demo.funding.repository.FundingDslRepository;
import NextLevel.demo.project.project.dto.response.ProjectListWithFundingDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDetailDto;
import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.repository.UserProjectDslRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageProjectSelectService {

    private final UserProjectDslRepository userProjectDslRepository;
    private final FundingDslRepository fundingDslRepository;

    public ResponseProjectListDto mypageProjectList(RequestMyPageProjectListDto dto) {
        return userProjectDslRepository.myProject(dto);
    }

    public ProjectListWithFundingDto mapageProjectListWithFunding(RequestMyPageProjectListDto dto) {
        ResponseProjectListDto projectList = userProjectDslRepository.myProject(dto);

        Long userId = dto.getUserId();
        if(dto.getType().equals(MyPageProjectListType.PROJECT))
            userId = null;

        fundingDslRepository.addFundingData(projectList.getProjects().stream().map(ResponseProjectListDetailDto::getProjectEntity).toList(), userId);

        return ProjectListWithFundingDto.of(projectList);
    }

}
