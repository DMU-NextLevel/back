package NextLevel.demo.admin.project;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseFundingDataDto {

    private String projectTitle;
    private String userNickName;
    private Long price;
    private String type;
    LocalDate createdAt;

    public static ResponseFundingDataDto of(FundingDataDao fundingDataDao) {
        ResponseFundingDataDto dto = new ResponseFundingDataDto();
        dto.projectTitle = fundingDataDao.getProjectTitle();
        dto.userNickName = fundingDataDao.getUserNickName();
        dto.price = fundingDataDao.getPrice();
        dto.type = fundingDataDao.getType();
        dto.createdAt = fundingDataDao.getCreatedAt();
        return dto;
    }

}
