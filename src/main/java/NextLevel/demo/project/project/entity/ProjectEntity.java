package NextLevel.demo.project.project.entity;

import NextLevel.demo.BasedEntity;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.funding.entity.FreeFundingEntity;
import NextLevel.demo.option.OptionEntity;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.community.entity.ProjectCommunityAskEntity;
import NextLevel.demo.project.notice.entity.ProjectNoticeEntity;
import NextLevel.demo.project.story.entity.ProjectStoryEntity;
import NextLevel.demo.project.tag.entity.ProjectTagEntity;
import NextLevel.demo.project.view.ProjectViewEntity;
import NextLevel.demo.user.entity.LikeEntity;
import NextLevel.demo.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
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

    @ManyToOne(targetEntity = ImgEntity.class, fetch = FetchType.LAZY) // project list api에서 N+1을 발생 시킴
    @JoinColumn(name = "img_id")
    private ImgEntity titleImg;

    @Column(nullable = false)
    private Long goal;

    @Column(nullable = false)
    private LocalDate expiredAt;

    @Column(nullable = false)
    private LocalDate startAt;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private List<ProjectTagEntity> tags;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private List<ProjectStoryEntity> stories;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<OptionEntity> options;

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

    public void setTags(List<ProjectTagEntity> tags) {
        this.tags = tags;
    }
    public void setFundingData(ProjectEntity project) {this.freeFundings = project.getFreeFundings();this.options = project.getOptions();}
    public void updateStatus(ProjectStatus status) { this.projectStatus = status; }

    @Builder
    public ProjectEntity(Long id, UserEntity user, String title, String content,
                         Long goal, ImgEntity titleImg, LocalDate expiredAt, LocalDate startAt, List<ProjectTagEntity> tags,
                         List<ProjectStoryEntity> stories) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.titleImg = titleImg;
        this.expiredAt = expiredAt;
        this.startAt = startAt;
        this.tags = tags;
        this.stories = stories;

        this.projectStatus = startAt.isAfter(LocalDate.now()) ? ProjectStatus.PENDING : ProjectStatus.PROGRESS;
        if(startAt.isAfter(expiredAt)) // start > expired
            throw new CustomException(ErrorCode.START_MUST_BEFORE_EXPIRED);
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
            "id=" + id +
           // ", user=" + user +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
           // ", titleImg=" + titleImg +
            ", tags=" + tags +
           // ", imgs=" + stories +
            '}';
    }
}
