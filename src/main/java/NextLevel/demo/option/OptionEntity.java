package NextLevel.demo.option;

import NextLevel.demo.funding.entity.OptionFundingEntity;
import NextLevel.demo.project.project.entity.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`option`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @ManyToOne(targetEntity = ProjectEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_id")
    private ProjectEntity project;

    @OneToMany(mappedBy = "option", fetch = FetchType.LAZY)
    private Set<OptionFundingEntity> fundings;

    public void update(SaveOptionRequestDto dto) {
        if(dto.getDescription() != null && !dto.getDescription().isEmpty())
            this.description = dto.getDescription();
        if(dto.getPrice() != null)
            this.price = dto.getPrice();
    }

    @Override
    public String toString() {
        return "OptionEntity{" +
            "id=" + id +
            ", price=" + price +
            ", description='" + description + '\'' +
            '}';
    }
}
