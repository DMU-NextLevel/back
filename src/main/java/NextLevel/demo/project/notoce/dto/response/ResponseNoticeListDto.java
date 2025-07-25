package NextLevel.demo.project.notoce.dto.response;

import NextLevel.demo.project.notoce.entity.ProjectNoticeEntity;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class ResponseNoticeListDto {

    private int noticeCount;
    private List<ResponseProjectNoticeDto> notices;

    public ResponseNoticeListDto(Set<ProjectNoticeEntity> entities) {
        this.notices = entities.stream().map(e->ResponseProjectNoticeDto.of(e)).toList();
        this.noticeCount = notices.size();
    }
}
