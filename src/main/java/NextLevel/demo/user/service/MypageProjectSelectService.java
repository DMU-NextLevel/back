package NextLevel.demo.user.service;

import NextLevel.demo.project.project.dto.response.ResponseProjectListDto;
import NextLevel.demo.user.dto.user.request.RequestMyPageProjectListDto;
import NextLevel.demo.user.repository.MyPageProjectListType;
import NextLevel.demo.user.repository.UserProjectDslRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageProjectSelectService {

    private final UserProjectDslRepository userProjectDslRepository;

    public ResponseProjectListDto mypageProjectList(@Valid RequestMyPageProjectListDto dto) {
        ResponseProjectListDto result = userProjectDslRepository.myProject(dto);

        if(dto.getType().equals(MyPageProjectListType.VIEW)){
            // sort !!
            Collections.sort(result.getProjects(), (a, b)->{
                return a.getProjectViewCreateAt().isBefore(b.getProjectViewCreateAt())? 1:-1;
            });
        }

        return result;
    }

}
