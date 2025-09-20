package NextLevel.demo.follow;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserValidateService userValidateService;

    public void follow(long userId, long targetId, boolean follow) {
        if(userId == targetId)
            throw new CustomException(ErrorCode.CAN_NOT_FOLLOW_SELF);

        UserEntity user = userValidateService.getUserInfoWithAccessToken(userId);
        UserEntity targetUser = userValidateService.findUserWithUserId(targetId);
        Optional<FollowEntity> followOpt = followRepository.findByUserIdAndTargetId(user.getId(), targetUser.getId());

        if(followOpt.isEmpty() && follow) {
            followRepository.save(
                    FollowEntity
                            .builder()
                            .user(user)
                            .target(targetUser)
                            .build()
            );
        }
        if(followOpt.isPresent() && !follow) {
            followRepository.deleteById(followOpt.get().getId());
        }
    }

}
