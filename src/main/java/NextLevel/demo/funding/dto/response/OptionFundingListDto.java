package NextLevel.demo.funding.dto.response;

import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.option.OptionDto;
import NextLevel.demo.option.OptionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
// option 정보, List<option funding dto>
public class OptionFundingListDto {
    private OptionDto option;
    private List<OptionFundingDto> optionFunding;

    @JsonIgnore
    private Long price;

    @JsonIgnore
    private Long count;

    public static OptionFundingListDto of(OptionEntity option){
        OptionFundingListDto dto = new OptionFundingListDto();
        dto.option = OptionDto.of(option);

        //dto.optionFunding = option.getFundings().stream().map(OptionFundingDto::of).toList();
        Long price = 0L;
        Long count = 0L;
        List<OptionFundingDto> optionFundingDtoList = new ArrayList<>();

        for(OptionFundingEntity optionFunding : option.getFundings()){
            count++;
            price += optionFunding.getOption().getPrice() * optionFunding.getCount();
            optionFundingDtoList.add(OptionFundingDto.of(optionFunding));
        }

        dto.optionFunding = optionFundingDtoList;
        dto.price = price;
        dto.count = count;

        return dto;
    }
}
