package NextLevel.demo.user.dto;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.role.UserRole;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private String name;
    @NotEmpty
    private String nickName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String key;
    private String address;
    private String number;
    private String areaNumber;

    @NotEmpty
    private String password;
    private String socialProvider;
    private String socialId;

    private MultipartFile img;
    private ImgEntity imgEntity;

    private String role = UserRole.SOCIAL.name();

    //used by social register
    @Builder
    public RequestUserCreateDto(Long id, String name, String nickName, String email, String address, String number, String areaNumber,
        String password, String socialProvider, String socialId, ImgEntity imgEntity) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.address = address;
        this.number = number;
        this.areaNumber = areaNumber;
        this.password = password;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.imgEntity = imgEntity;
    }

    public UserEntity toUserEntity() {
        return new UserEntity(id, name, nickName, 0, address, number, areaNumber, imgEntity);
    }

    public UserDetailEntity toUserDetailEntity(UserEntity user){
        return new UserDetailEntity(user, UUID.randomUUID().toString(), role, email, password, socialProvider, socialId);
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
