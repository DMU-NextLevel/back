package NextLevel.demo.option;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionDto {
    private Long id;
    private Integer price;
    private String description;

    public static OptionDto of(OptionEntity entity) {
        OptionDto dto = new OptionDto();
        dto.id =entity.getId();
        dto.price = entity.getPrice();
        dto.description = entity.getDescription();
        return dto;
    }
}
