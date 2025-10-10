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

    public UserSocialProfileDto selectUserSocialProfile(long userId) {
        UserEntity user = validateService.findUserWithUserId(userId);
        return selectUserSocialProfile(user);
    }

    public UserSocialProfileDto selectUserSocialProfile(UserEntity user) {
        return UserSocialProfileDto.of(user, followRepository.followCount(user.getId()));
    }
}
