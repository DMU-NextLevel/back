package NextLevel.demo.admin.user;

import NextLevel.demo.img.ImgDto;
import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseFullUserInfoDto {

    private Long id;

    private String name;
    private String nickName;
    private int point;
    private String address;
    private String number;
    private String areaNumber;

    private String email;
    private String socialProvider;

    private ImgDto img;

    private String role;

    public static ResponseFullUserInfoDto of(UserEntity userFullEntity) {
        UserDetailEntity detail = userFullEntity.getUserDetail();

        ResponseFullUserInfoDto dto = new ResponseFullUserInfoDto(userFullEntity.getId(), userFullEntity.getName(), userFullEntity.getNickName(), userFullEntity.getPoint(),
                userFullEntity.getAddress(), userFullEntity.getNumber(), userFullEntity.getAreaNumber(), detail.getEmail(),
                detail.getSocialProvider(), userFullEntity.getRole());

        dto.img = new ImgDto(userFullEntity.getImg());

        return dto;
    }

    private ResponseFullUserInfoDto(Long userId, String name, String nickName, int point, String address,
                                      String number, String areaNumber, String email, String socialProvider, String role) {
        this.id = userId;
        this.name = name;
        this.nickName = nickName;
        this.point = point;
        this.address = address;
        this.number = number;
        this.areaNumber = areaNumber;
        this.email = email;
        this.socialProvider = socialProvider;
        this.role = role;
    }

}
