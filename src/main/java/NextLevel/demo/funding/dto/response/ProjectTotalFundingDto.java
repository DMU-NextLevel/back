package NextLevel.demo.funding.dto.response;

import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.option.OptionEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectTotalFundingDto {

    private ProjectFundingDto total; // count, price
    private List<OptionFundingListDto> optionFunding; // List<OptionEntity> (with OptionFunding, User, Coupon)
    private List<FreeFundingDto> freeFunding; // List<FreeFundingEntity>

    public static ProjectTotalFundingDto of(List<OptionEntity> optionList, List<FreeFundingEntity> freeFundingList) {
        ProjectTotalFundingDto dto = new ProjectTotalFundingDto();

        Long count = 0L;
        Long price = 0L;
        List<OptionFundingListDto> optionFundingList = new ArrayList<>();
        for(OptionEntity option : optionList){
            OptionFundingListDto listDto = OptionFundingListDto.of(option);
            optionFundingList.add(listDto);
            count += listDto.getCount();
            price += listDto.getPrice();
        }

        List<FreeFundingDto> freeFundingDtoList = new ArrayList<>();
        for(FreeFundingEntity freeFunding : freeFundingList){
            FreeFundingDto freeFundingDto = FreeFundingDto.of(freeFunding);
            freeFundingDtoList.add(freeFundingDto);
            count++;
            price += freeFundingDto.getPrice();
        }

        dto.total = new ProjectFundingDto(count, price);

        return dto;
    }

}
