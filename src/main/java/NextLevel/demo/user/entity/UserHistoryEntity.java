package NextLevel.demo.user.entity;

import NextLevel.demo.BasedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name= "user_history")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@SQLDelete(sql="update user_history set delete_at = now() where id = ?")
@SQLRestriction("delete_at IS NULL")
public class UserHistoryEntity extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @Column
    private Date date;

    @Column
    private String uri;

    @Column
    private String ip;

    @Column
    private String agent;

}
