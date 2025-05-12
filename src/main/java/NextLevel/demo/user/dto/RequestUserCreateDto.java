package NextLevel.demo.user.dto;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.role.UserRole;
import jakarta.validation.constraints.NotEmpty;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class RequestUserCreateDto {
    private Long id;

    private String name;
    private String nickName;
    @NotEmpty
    private String email;
    private String key;
    private String address;
    private String number;

    private String password;
    private String socialProvider;
    private String socialId;

    private MultipartFile img;
    private ImgEntity imgEntity;

    private String role = UserRole.SOCIAL.name();

    @Builder
    public RequestUserCreateDto(Long id, String name, String nickName, String email, String address, String number,
        String password, String socialProvider, String socialId, ImgEntity imgEntity) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.address = address;
        this.number = number;
        this.password = password;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.imgEntity = imgEntity;
    }

    public void validateNumberFormat() {
        if(number.matches("^010-\\d{4}-\\d{4}$"))
            number = number.replaceAll("-", "");
        if(!number.matches("^010\\d{8}$"))
            throw new CustomException(ErrorCode.INVALID_NUMBER_FORMAT);
    }

    public UserEntity toUserEntity() {
        return new UserEntity(id, name, nickName, 0, address, number, imgEntity);
    }

    public UserDetailEntity toUserDetailEntity(UserEntity user){
        return new UserDetailEntity(user, UUID.randomUUID().toString(), role, email, password, socialProvider, socialId);
    }

    public boolean validateAllData() {
        if(name != null && !name.isEmpty() && nickName != null && !nickName.isEmpty()
            && address != null && !address.isEmpty() && number != null && !number.isEmpty())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "RequestUserCreateDto{" +
            "name='" + name + '\'' +
            ", nickName='" + nickName + '\'' +
            ", email='" + email + '\'' +
            ", address='" + address + '\'' +
            ", number='" + number + '\'' +
            ", password='" + password + '\'' +
            ", socialProvider='" + socialProvider + '\'' +
            ", socialId='" + socialId + '\'' +
            ", img='" + img.toString() + '\'' +
            '}';
    }
}
