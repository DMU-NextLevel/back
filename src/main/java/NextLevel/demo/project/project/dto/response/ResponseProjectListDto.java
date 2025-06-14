package NextLevel.demo.project.project.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResponseProjectListDto {
    private List<ResponseProjectListDetailDto> projects;
    private long totalCount; // projects 에 존재함
    private long pageCount; // page 당 반환 project 갯수
    private long page; // 요청시 들어옴

    public ResponseProjectListDto(List<ResponseProjectListDetailDto> projects) {
        this.projects = projects;
    }

    public void setPageCount(long pageCount, long page) {
        this.pageCount = pageCount;
        this.page = page;
        if(projects != null && projects.size() > 0) {
            this.totalCount = projects.get(0).getTotalCount();
        }
    }
}
