package NextLevel.demo.project.project.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProjectListWithFundingDto {
    private List<ProjectListDetailWithFundingDto> projects;
    private long totalCount; // projects 에 존재함
    private long pageCount; // page 당 반환 project 갯수
    private long page; // 요청시 들어옴

    public static ProjectListWithFundingDto of(ResponseProjectListDto projectListDto) {
        ProjectListWithFundingDto dto = new ProjectListWithFundingDto();
        dto.projects = projectListDto.getProjects().stream().map(ProjectListDetailWithFundingDto::of).toList();
        dto.totalCount = projectListDto.getTotalCount();
        dto.pageCount = projectListDto.getPageCount();
        dto.page = projectListDto.getPage();
        return dto;
    }
}
