package NextLevel.demo.social.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.follow.SelectSocialProfileService;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.social.dto.RequestSocialCreateDto;
import NextLevel.demo.social.dto.ResponseSocialDto;
import NextLevel.demo.social.dto.SocialLikeDto;
import NextLevel.demo.social.dto.SocialListDto;
import NextLevel.demo.social.entity.SocialEntity;
import NextLevel.demo.social.entity.SocialImgEntity;
import NextLevel.demo.social.entity.SocialLikeEntity;
import NextLevel.demo.social.repository.SocialImgRepository;
import NextLevel.demo.social.repository.SocialLikeRepository;
import NextLevel.demo.social.repository.SocialRepository;
import NextLevel.demo.social.repository.IsSelectSocialLikedListInterface;
import NextLevel.demo.user.dto.user.response.UserSocialProfileDto;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final SocialRepository socialRepository;

    private final UserValidateService userValidateService;

    private final ImgService imgService;
    private final SocialImgRepository socialImgRepository;

    private final EntityManager entityManager;
    private final SocialLikeRepository socialLikeRepository;
    private final SelectSocialProfileService selectSocialProfileService;

    @ImgTransaction
    @Transactional
    public void create(RequestSocialCreateDto dto, ArrayList<Path> imgPaths) {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        SocialEntity social = socialRepository.save(dto.toEntity(user));

        if(dto.getImg() != null && !dto.getImg().isEmpty())
            saveImgs(dto.getImg(), social, imgPaths);
    }

    @ImgTransaction
    @Transactional
    public void update(RequestSocialCreateDto dto, ArrayList<Path> imgPaths) {
        SocialEntity social = socialRepository.findById(dto.getId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "social");}
        );

        if(!social.getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        if(dto.getImg() != null && !dto.getImg().isEmpty() && !dto.getImg().get(0).isEmpty()){
            deleteImgs(social.getId(), social.getImgs().stream().map(SocialImgEntity::getImg).toList());
            saveImgs(dto.getImg(), social, imgPaths);
        }

        social.update(dto);
    }

    @Transactional
    public void delete(Long socialId, Long userId) {
        SocialEntity social = socialRepository.findById(socialId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "social");}
        );

        if(!social.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        deleteImgs(socialId, social.getImgs().stream().map(SocialImgEntity::getImg).toList());

        entityManager.flush();
        entityManager.clear();

        socialRepository.deleteById(social.getId());
    }

    public void socialLike( SocialLikeDto dto) {
        SocialEntity social = socialRepository.findById(dto.getSocialId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "social");}
        );
        Optional<SocialLikeEntity> oldLikeOpt = socialLikeRepository.findByUserIdAndSocialId(dto.getUserId(), dto.getSocialId());
        if(dto.getLike() && oldLikeOpt.isEmpty()) {
            // 좋야요를 누름
            socialLikeRepository.save(SocialLikeEntity
                    .builder()
                    .user(entityManager.getReference(UserEntity.class, dto.getUserId()))
                    .social(social)
                    .build());
        }
        if(!dto.getLike() && oldLikeOpt.isPresent()) {
            // 좋아요 취소
            socialLikeRepository.delete(oldLikeOpt.get());
        }
    }

    public SocialListDto list(Long targetUserId, Long userId) {
        UserEntity targetUser = userValidateService.findUserWithUserId(targetUserId);

        List<SocialEntity> socialEntityList = socialRepository.findAllByUserId(targetUserId);
        List<IsSelectSocialLikedListInterface> isSocialLikedList = socialRepository.isSelectSocialLikeList(socialEntityList.stream().map(SocialEntity::getId).toList(), userId);

        Map<Long, IsSelectSocialLikedListInterface> isSocialLikedMap = new HashMap<>();
        isSocialLikedList.forEach(isLikedInterface->
                isSocialLikedMap.put(
                        isLikedInterface.getSocialId(),
                        isLikedInterface
                )
        );

        List<ResponseSocialDto> socials = socialEntityList.stream().map(entity->
                new ResponseSocialDto(
                        entity,
                        isSocialLikedMap.get(entity.getId()).getTotalLikeCount(),
                        isSocialLikedMap.get(entity.getId()).getMyLikeCount()
                )).toList();

        UserSocialProfileDto user = selectSocialProfileService.selectUserSocialProfile(targetUser, userId);
        return SocialListDto.of(user, socials);
    }

    private void saveImgs(List<MultipartFile> imgFiles, SocialEntity social, ArrayList<Path> imgPaths) {
        if(imgFiles.isEmpty())
            return;
        imgFiles.forEach(imgFile ->
            socialImgRepository.save(
                    SocialImgEntity
                            .builder()
                            .social(social)
                            .img(imgService.saveImg(imgFile, imgPaths))
                            .build()
            )
        );
    }

    private void deleteImgs(Long socialId, List<ImgEntity> imgs) {
        socialImgRepository.deleteAllBySocialId(socialId);
        imgs.forEach(img->imgService.deleteImg(img));
    }

}
