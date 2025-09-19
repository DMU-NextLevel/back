package NextLevel.demo.project.notice.dto.response;

import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class ResponseNoticeListDto {

    private int noticeCount;
    private List<ResponseProjectNoticeDto> notices;

    public ResponseNoticeListDto(List<ProjectNoticeEntity> entities) {
        this.notices = entities.stream().map(e->ResponseProjectNoticeDto.of(e)).toList();
        this.noticeCount = notices.size();
    }
}
