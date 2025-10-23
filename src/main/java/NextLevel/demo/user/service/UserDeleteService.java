package NextLevel.demo.user.service;

import NextLevel.demo.follow.FollowService;
import NextLevel.demo.funding.service.FundingRollbackService;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.social.service.SocialService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserHistoryRepository;
import NextLevel.demo.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeleteService {

    private final UserValidateService  userValidateService;
    private final UserRepository userRepository;
    private final FollowService followService;
    private final FundingRollbackService fundingRollbackService;
    private final SocialService socialService;
    private final ProjectDeleteService projectDeleteService;
    private final ImgService imgService;
    private final UserHistoryRepository userHistoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @ImgTransaction
    public void deleteUser(Long userId, ImgPath imgPath) {
        UserEntity user = userValidateService.findUserWithUserId(userId);

        // project 삭제
        projectDeleteService.deleteAllProjectByUser(user, imgPath);

        // funding 삭제
        fundingRollbackService.rollbackByUser(user);

        // social삭제
        socialService.deleteAllByUser(user, imgPath);

        // follow (user like)삭제
        followService.deleteFollowByUserId(user);

        // user history
        userHistoryRepository.deleteAllByUser(user);

        // coupon -> cascade
        //user_detail삭제 -> cascade 적용

        ImgEntity img = user.getImg();

        //user 삭제
        userRepository.delete(user);
        entityManager.flush();

        imgService.deleteImg(img, imgPath);
    }
}
