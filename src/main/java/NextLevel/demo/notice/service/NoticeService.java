package NextLevel.demo.notice.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.notice.dto.SaveNoticeDto;
import NextLevel.demo.notice.entity.NoticeEntity;
import NextLevel.demo.notice.entity.NoticeImgEntity;
import NextLevel.demo.notice.repository.NoticeImgRepository;
import NextLevel.demo.notice.repository.NoticeRepository;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ImgService imgService;
    private final NoticeImgRepository noticeImgRepository;

    public List<NoticeEntity> getAllNotice() {
        return noticeRepository.findAll();
    }

    @ImgTransaction
    @Transactional
    public void addNotice(SaveNoticeDto dto, ImgPath imgPath) {
        NoticeEntity newNotice = dto.toEntity();
        List<ImgEntity> newImgs = new ArrayList<ImgEntity>();
        if(dto.getImgs()!=null && !dto.getImgs().isEmpty()) {
            dto.getImgs().stream().forEach(file -> {
                if(file != null && !file.isEmpty())
                    newImgs.add(imgService.saveImg(file, imgPath));
            });
            newNotice.setImgs(
                newImgs
                    .stream()
                    .map(imgEntity ->
                        NoticeImgEntity
                            .builder()
                            .img(imgEntity)
                            .notice(newNotice)
                            .build()
                    )
                    .toList()
            );
        }
        noticeRepository.save(newNotice);
    }

    @Transactional
    public void updateNotice(SaveNoticeDto dto) {
        NoticeEntity oldNotice = noticeRepository.findById(dto.getId()).orElseThrow(
            () -> {
                throw new CustomException(ErrorCode.NOT_FOUND, "notice");
            }
        );

        if (dto.getContent() != null && !dto.getContent().isEmpty())
            oldNotice.setContent(dto.getContent());
        if (dto.getTitle() != null && !dto.getTitle().isEmpty())
            oldNotice.setTitle(dto.getTitle());

        noticeRepository.save(oldNotice);
    }

    @ImgTransaction
    @Transactional
    public void updateImg(Long noticeId, List<MultipartFile> imgFiles, ImgPath imgPath) {
        NoticeEntity notice = noticeRepository.findById(noticeId).orElseThrow(
            () -> {throw new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );
        List<NoticeImgEntity> oldNoticeImgs = notice.getImgs();
        List<ImgEntity> oldImgs = oldNoticeImgs.stream().map(NoticeImgEntity::getImg).toList();

        List<ImgEntity> newImgs = new ArrayList<>();
        imgFiles.stream().forEach(i->newImgs.add(imgService.saveImg(i, imgPath)));
        List<NoticeImgEntity> newNoticeImgs = new ArrayList<>();
        newImgs
            .stream()
            .forEach(imgEntity->
                newNoticeImgs.add(
                NoticeImgEntity
                    .builder()
                    .img(imgEntity)
                    .notice(notice)
                    .build()
                )
            );

        noticeImgRepository.saveAll(newNoticeImgs);
        noticeImgRepository.deleteAllById(oldNoticeImgs.stream().map(NoticeImgEntity::getId).toList());
        oldImgs.stream().forEach((img)->imgService.deleteImg(img, imgPath));
    }

    //@Transactional
    public void removeNotice(Long noticeId, ImgPath imgPath) {
        NoticeEntity notice = noticeRepository.findById(noticeId).orElseThrow(
            () -> {throw new CustomException(ErrorCode.NOT_FOUND, "notice");}
        );
        List<ImgEntity> oldImgs = notice.getImgs().stream().map(NoticeImgEntity::getImg).toList();

        noticeRepository.delete(notice);

        oldImgs.forEach((img)->imgService.deleteImg(img, imgPath));
    }
}
