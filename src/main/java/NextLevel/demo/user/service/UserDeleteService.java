package NextLevel.demo.user.service;

import NextLevel.demo.follow.FollowService;
import NextLevel.demo.funding.service.FundingRollbackService;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.project.project.service.ProjectDeleteService;
import NextLevel.demo.social.service.SocialService;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.repository.UserRepository;
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

    @Transactional
    @ImgTransaction
    public void deleteUser(Long userId, ImgPath imgPath) {
        UserEntity user = userValidateService.findUserWithUserId(userId);

        // project 삭제
        projectDeleteService.deleteAllProjectByUser(user);

        // funding 삭제
        fundingRollbackService.rollbackByUser(user);

        // social삭제
        socialService.deleteAllByUser(user);

        // follow (user like)삭제
        followService.deleteFollowByUserId(user);

        // img 삭제
        imgService.deleteImg(user.getImg(), imgPath);

        // coupon -> cascade
        //user_detail삭제 -> cascade 적용

        //user 삭제
        userRepository.delete(user);
    }
}
