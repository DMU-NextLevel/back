package NextLevel.demo.funding.entity;

import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    @Column
    private Long price;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "option_funding_id", nullable = true)
    private OptionFundingEntity optionFunding;

    public void rollBackUseCoupon() {
        optionFunding = null;
    }

    public void updateProjectFundingEntity(OptionFundingEntity optionFunding) {
        this.optionFunding = optionFunding;
    }

}

/*
option 펀딩을 한다
user coupon <-> option funding entity 와 1대 1 관계 (관계의 주인 : coupon)
option funding을 취소 한다 -> update coupon의 option_funding_id = null

coupon list 조회 : coupon.option_funding.id is null 인 값들만 렌더링 한다
 */
