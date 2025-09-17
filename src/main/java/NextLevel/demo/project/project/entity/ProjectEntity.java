package NextLevel.demo.project.project.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.view.ProjectViewEntity;
import NextLevel.demo.user.entity.LikeEntity;
import NextLevel.demo.user.entity.UserEntity;
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
import jakarta.persistence.Table;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(targetEntity = ImgEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "img_id")
    private ImgEntity titleImg;

    @Column(nullable = false)
    private Long goal;

    @Column(nullable = false)
    private LocalDateTime expired;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private List<ProjectTagEntity> tags;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private List<ProjectStoryEntity> stories;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<OptionEntity> options;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<FreeFundingEntity> freeFundings;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<LikeEntity> likes;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectCommunityAskEntity> communities;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectNoticeEntity> notices;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectViewEntity> views;

    public void setStories(List<ProjectStoryEntity> imgs) {
        this.stories = imgs;
    }
    public void setTags(List<ProjectTagEntity> tags) {
        this.tags = tags;
    }

    @Builder
    public ProjectEntity(Long id, UserEntity user, String title, String content,
        Long goal, ImgEntity titleImg, String expired, List<ProjectTagEntity> tags,
        List<ProjectStoryEntity> stories) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.titleImg = titleImg;
        this.expired = LocalDate.parse(expired, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23, 59); //LocalDateTime.of(new SimpleDateFormat("yyyy-MM-dd").parse(expired));
        this.tags = tags;
        this.stories = stories;
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
            "id=" + id +
            ", user=" + user +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", titleImg=" + titleImg +
            ", tags=" + tags +
            ", imgs=" + stories +
            '}';
    }
}
