package NextLevel.demo.social.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.social.dto.RequestSocialCreateDto;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "social")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
// social hard delete
public class SocialEntity extends BasedEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = true)
    private String text;

    @OneToMany(mappedBy = "social")
    private List<SocialImgEntity> imgs;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "social", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<SocialLikeEntity> likes;

    public void update(RequestSocialCreateDto dto) {
        if(dto.getText()!=null && !dto.getText().isEmpty())
            this.text = dto.getText();
    }

}
