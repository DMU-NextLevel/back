package NextLevel.demo.project.community.dto.response;

import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

@Getter
public class ResponseCommunityListDto {
    private int communityCount;
    private List<ResponseProjectCommunityDto> communities;

    public ResponseCommunityListDto (List<ProjectCommunityAskEntity> communities) {
        this.communities = communities.stream().map(e->ResponseProjectCommunityDto.of(e)).toList();
        this.communityCount = communities.size();
    }
}

