package NextLevel.demo.admin.project;

import java.time.LocalDate;

public interface FundingDataDao {

    String getProjectTitle();
    String getUserNickName();
    Long getPrice();
    String getType();
    LocalDate getCreatedAt();

}
