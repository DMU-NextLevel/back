package NextLevel.demo.project.project.dto.response;

import NextLevel.demo.funding.dto.response.FreeFundingDto;
import NextLevel.demo.funding.dto.response.OptionFundingListDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter @Getter
public class ProjectListDetailWithFundingDto {

    private ResponseProjectListDetailDto project;
    private List<OptionFundingListDto> optionFunding;
    private FreeFundingDto freeFunding;

    public static ProjectListDetailWithFundingDto of(ResponseProjectListDetailDto projectListDetailDto) {
        ProjectListDetailWithFundingDto dto = new  ProjectListDetailWithFundingDto();
        dto.project = projectListDetailDto;
        dto.optionFunding = projectListDetailDto.getProjectEntity().getOptions().stream().map(OptionFundingListDto::of).toList();
        dto.freeFunding = FreeFundingDto.of(projectListDetailDto.getProjectEntity().getFreeFundings().stream().findFirst().get());
        return dto;
    }

}

/*
    project list detail dto
    List<option funding list dto>
        option dto,
        option funding dto
    free funding dto
 */