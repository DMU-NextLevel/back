package NextLevel.demo.message;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="kakao_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KakaoTokenEntity {
    @Id
    private Long id;

    private String access;
    private String refresh;

    private Date updateAt;

    @Builder
    public KakaoTokenEntity(String refresh, String access) {
        this.id = 1L;
        this.updateAt = new Date();
        this.refresh = refresh;
        this.access = access;
    }
}
