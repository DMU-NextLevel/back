package NextLevel.demo.funding.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "option_funding")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class OptionFundingEntity extends BasedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = OptionEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "option_id")
    private OptionEntity option;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne(mappedBy = "optionFunding", targetEntity = CouponEntity.class, fetch = FetchType.LAZY)
    private CouponEntity coupon;

    @Column
    private long count;

    public void updateCount(long count) {
        this.count += count;
    }

    @Override
    public String toString() {
        return "FundingEntity{" +
            "id=" + id +
            ", count=" + count +
            '}';
    }
}
