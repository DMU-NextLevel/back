package NextLevel.demo.social.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.social.dto.RequestSocialCreateDto;
import NextLevel.demo.social.dto.ResponseSocialDto;
import NextLevel.demo.social.entity.SocialEntity;
import NextLevel.demo.social.entity.SocialImgEntity;
import NextLevel.demo.social.repository.SocialImgRepository;
import NextLevel.demo.social.repository.SocialRepository;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.service.UserValidateService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final SocialRepository socialRepository;

    private final UserValidateService userValidateService;

    private final ImgService imgService;
    private final SocialImgRepository socialImgRepository;

    private final EntityManager entityManager;

    @ImgTransaction
    @Transactional
    public void create(RequestSocialCreateDto dto, ImgPath imgPath) {
        UserEntity user = userValidateService.getUserInfoWithAccessToken(dto.getUserId());
        SocialEntity social = socialRepository.save(dto.toEntity(user));

        saveImgs(dto.getImgs(), social, imgPath);
    }

    @ImgTransaction
    @Transactional
    public void update(RequestSocialCreateDto dto, ImgPath imgPath) {
        SocialEntity social = socialRepository.findById(dto.getId()).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "social");}
        );

        if(!social.getUser().getId().equals(dto.getUserId()))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        if(dto.getImgs() != null && !dto.getImgs().isEmpty()){
            deleteImgs(social.getId(), social.getImgs().stream().map(SocialImgEntity::getImg).toList(), imgPath);
            saveImgs(dto.getImgs(), social, imgPath);
        }

        social.update(dto);
    }

    @Transactional
    @ImgTransaction
    public void delete(Long socialId, Long userId, ImgPath imgPath) {
        SocialEntity social = socialRepository.findById(socialId).orElseThrow(
                ()->{return new CustomException(ErrorCode.NOT_FOUND, "social");}
        );

        if(!social.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.NOT_AUTHOR);

        deleteImgs(socialId, social.getImgs().stream().map(SocialImgEntity::getImg).toList(), imgPath);

        entityManager.flush();
        entityManager.clear();

        socialRepository.deleteById(social.getId());
    }

    public List<ResponseSocialDto> list(Long userId) {
        List<SocialEntity> socials = socialRepository.findAllByUserId(userId);
        return socials.stream().map(ResponseSocialDto::of).toList();
    }

    private void saveImgs(List<MultipartFile> imgFiles, SocialEntity social, ImgPath imgPath) {
        imgFiles.forEach(imgFile ->
            socialImgRepository.save(
                    SocialImgEntity
                            .builder()
                            .social(social)
                            .img(imgService.saveImg(imgFile, imgPath))
                            .build()
            )
        );
    }

    private void deleteImgs(Long socialId, List<ImgEntity> imgs, ImgPath imgPath) {
        socialImgRepository.deleteAllBySocialId(socialId);
        imgs.forEach(img->imgService.deleteImg(img, imgPath));
    }

}
