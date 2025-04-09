package NextLevel.demo.project.dto.response;

import NextLevel.demo.project.entity.ProjectCommunityEntity;
import NextLevel.demo.project.entity.ProjectEntity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProjectCommunityDto {

    private Long id;

    private String ask;

    private Date askAt;

    private String answer;

    private Date answerAt;

    public static ResponseProjectCommunityDto of(ProjectCommunityEntity entity) {
        ResponseProjectCommunityDto dto = new ResponseProjectCommunityDto();
        dto.setId(entity.getId());
        dto.setAsk(entity.getAsk());
        dto.setAskAt(entity.getAskAt());
        dto.setAnswer(entity.getAnswer());
        dto.setAnswerAt(entity.getAnswerAt());
        return dto;
    }

}
