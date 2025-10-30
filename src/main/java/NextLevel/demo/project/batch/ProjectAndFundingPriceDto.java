package NextLevel.demo.project.batch;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.entity.ProjectEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProjectAndFundingPriceDto implements Serializable {

    private ProjectSerializableDto projectSerializableDto;
    private Integer fundingPrice;
    private ProjectStatus projectStatus;

    public ProjectAndFundingPriceDto(ProjectSerializableDto projectSerializableDto, Integer fundingPrice) {
        this.projectSerializableDto = projectSerializableDto;
        this.fundingPrice = fundingPrice;
    }
}
