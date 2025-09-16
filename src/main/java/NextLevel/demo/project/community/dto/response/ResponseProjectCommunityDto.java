package NextLevel.demo.project.community.dto.response;

import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ResponseProjectCommunityDto {
    private ResponseProjectCommunityAskDto ask;
    private ResponseProjectCommunityAnswerDto answer;

    public static ResponseProjectCommunityDto of(ProjectCommunityAskEntity askEntity) {
        ResponseProjectCommunityDto dto = new ResponseProjectCommunityDto();
        dto.ask = ResponseProjectCommunityAskDto.of(askEntity);
        if(askEntity.getAnswer() != null)
            dto.answer = ResponseProjectCommunityAnswerDto.of(askEntity.getAnswer());
        return dto;
    }
}
