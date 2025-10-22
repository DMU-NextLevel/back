package NextLevel.demo.follow;

import NextLevel.demo.user.dto.user.response.UserSocialProfileDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectSocialProfileService {

    private final FollowRepository followRepository;
    private final UserValidateService validateService;

    public UserSocialProfileDto selectUserSocialProfile(long targetUserId, Long userId) {
        UserEntity user = validateService.findUserWithUserId(targetUserId);
        return selectUserSocialProfile(user, userId);
    }

    public UserSocialProfileDto selectUserSocialProfile(UserEntity targetUser, Long userId) {
        SelectFollowCountAndIsFollowDao dao = followRepository.selectFollowCountAndFollowDao(targetUser.getId(), userId);
        return UserSocialProfileDto.of(targetUser, dao.getFollowCount(), dao.getIsFollow());
    }
}
