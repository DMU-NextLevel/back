package NextLevel.demo.project.project.dto.request;

import java.util.List;

import NextLevel.demo.project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Builder
//@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestMainPageProjectListDto {
    private Long userId;
    private List<Long> tag;
    private String order;
    private String search;
    private Boolean desc;
    private Integer page = 0;
    private Long pageCount = 8L; // default 8

    private List<ProjectStatus> status;

    public List<Long> getTagIds() {
        return tag;
    }

    public Long getLimit() {
        return pageCount;
    }
    public long getOffset() {
        if(page == null)
            page = 0;
        return pageCount * page;
    }

    public void setTag(List<Long> tag) {
        if(tag ==null || tag.isEmpty())
            return;
        this.tag = tag;
    }

    public void setPage(Integer page) {
        if(page == null)
            return;
        this.page = page;
    }

    public void setPageCount(Long pageCount) {
        if(pageCount == null)
            return;
        this.pageCount = pageCount;
    }

}
