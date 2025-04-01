package NextLevel.demo.user.dto.user;

import NextLevel.demo.user.entity.UserDetailEntity;
import NextLevel.demo.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserInfoDto {
    private String name;
    private String nickName;
    private int point;
    private String address;
    private String number;

    private String email;
    private String socialProvider;

    private String img;

    public static ResponseUserInfoDto of(UserEntity userFullEntity) {
        UserDetailEntity detail = userFullEntity.getUserDetail();

        ResponseUserInfoDto dto = new ResponseUserInfoDto(userFullEntity.getName(), userFullEntity.getNickName(), userFullEntity.getPoint(),
            userFullEntity.getAddress(), userFullEntity.getNumber(), detail.getEmail(),
            detail.getSocialProvider());

        if(userFullEntity.getImg() != null) {
            dto.setImg(userFullEntity.getImg().getUri());
        }

        return dto;
    }

    public ResponseUserInfoDto(String name, String nickName, int point, String address,
        String number, String email, String socialProvider) {
        this.name = name;
        this.nickName = nickName;
        this.point = point;
        this.address = address;
        this.number = number;
        this.email = email;
        this.socialProvider = socialProvider;
        this.img = img;
    }
}
