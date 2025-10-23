package NextLevel.demo.social.entity;

import NextLevel.demo.img.entity.ImgEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_img")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
// social_img hard delete
public class SocialImgEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "img_id")
    private ImgEntity img;

    @ManyToOne(cascade = {})
    @JoinColumn(name = "social_id")
    private SocialEntity social;
}
