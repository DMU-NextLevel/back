package NextLevel.demo.user.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.entity.CouponEntity;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql="update user set delete_at = now(), img_id = null where id = ?")
@SQLRestriction("delete_at IS NULL")
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

    // Enumerated(EnumType.STRING) 추후 수정
    @Column(length=5, columnDefinition = "char(6)")
    private String role = UserRole.SOCIAL.name();

    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {})
    @JoinColumn(name = "img_id", nullable = true)
    private ImgEntity img;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<UserDetailEntity> userDetail;

    @OneToMany(mappedBy= "user", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<CouponEntity> coupon;

    public UserDetailEntity getUserDetail() {
        return userDetail.getFirst();
    }

    public void setUserDetail(UserDetailEntity detailEntity) {
        userDetail = new ArrayList<>();
        userDetail.add(detailEntity);
    }

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
        if(role.equals(UserRole.ADMIN.name()))
            return;

        if(name != null && !name.isEmpty() &&
                nickName != null && !nickName.isEmpty() &&
                address != null && !address.isEmpty() &&
                number != null && !number.isEmpty())
            role = UserRole.USER.name();
        else
            role = UserRole.SOCIAL.name();
    }

    public void setNumber(String number) {
        this.number = StringUtil.getFormattedNumber(number, StringUtil.PHONE_NUMBER_FORMAT);
    }
    public void setAreaNumber(String areaNumber) { this.areaNumber = StringUtil.getFormattedNumber(areaNumber, StringUtil.AREA_NUMBER_FORMAT); }

    public void updatePoint(long point) { this.point+= point;}

    public boolean isAdmin() {
        return this.role.equals(UserRole.ADMIN.name());
    }

    public void updateUserInfo(String name, String value) {
        if(name.equals("email"))
            throw new CustomException(ErrorCode.CAN_NOT_CHANGE_EMAIL);

        try{
            java.lang.reflect.Method method = UserEntity.class.getDeclaredMethod(StringUtil.setGetterName(name), String.class);
            method.invoke(this, value);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.CAN_NOT_INVOKE, name);
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof CustomException)
                throw (CustomException) e.getTargetException();
            else
                throw new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, e.getTargetException().getMessage());
        }

        checkRole();
    }
}
