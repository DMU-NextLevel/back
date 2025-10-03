package NextLevel.demo.user.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.util.StringUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String nickName;

    @Column(nullable = false)
    @ColumnDefault("'0'")
    private int point;

    @Column
    private String address;

    @Column
    private String number;

    @Column
    private String areaNumber;

    @Column(length=5, columnDefinition = "char(6)")
    private String role = UserRole.SOCIAL.name();

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.REMOVE, optional = true)
    @JoinColumn(name = "img_id", nullable = true)
    private ImgEntity img;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserDetailEntity userDetail;

    @Builder
    public UserEntity(Long id, String name,String nickName, int point, String address, String number, String areaNumber, ImgEntity img) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.point = point;
        this.address = address;
        this.number = StringUtil.getFormattedNumber(number, StringUtil.PHONE_NUMBER_FORMAT);
        this.areaNumber = StringUtil.getFormattedNumber(areaNumber, StringUtil.AREA_NUMBER_FORMAT);
        this.img = img;
        checkRole();
    }

    public void checkRole() {
        if(name != null && !name.isEmpty() && nickName != null && !nickName.isEmpty()
            && address != null && !address.isEmpty() && number != null && !number.isEmpty())
            role = UserRole.USER.name();
        else
            role = UserRole.SOCIAL.name();
    }

    public void setNumber(String number) {
        this.number = StringUtil.getFormattedNumber(number, StringUtil.PHONE_NUMBER_FORMAT);
    }
    public void setAreaNumber(String areaNumber) { this.areaNumber = StringUtil.getFormattedNumber(areaNumber, StringUtil.AREA_NUMBER_FORMAT); }

    public void updatePoint(long point) { this.point+= point;}
}
