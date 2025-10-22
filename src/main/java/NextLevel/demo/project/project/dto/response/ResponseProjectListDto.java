package NextLevel.demo.project.project.dto.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResponseProjectListDto {
    private List<ResponseProjectListDetailDto> projects;
    private long totalCount;
    private long pageCount; // page 당 반환 project 갯수
    private long page; // 요청시 들어옴

    public ResponseProjectListDto(List<ResponseProjectListDetailDto> projects, long totalCount, long pageCount, long page) {
        this.projects = projects;
        this.pageCount = pageCount;
        this.page = page;
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "ResponseProjectListDto{" +
                "projects=" + Arrays.toString(projects.toArray()) +
                ", totalCount=" + totalCount +
                ", pageCount=" + pageCount +
                ", page=" + page +
                '}';
    }
}
