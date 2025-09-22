package NextLevel.demo.notice.controller;

import NextLevel.demo.common.SuccessResponse;
import NextLevel.demo.img.service.ImgTransaction;
import NextLevel.demo.notice.dto.ResponseNoticeDto;
import NextLevel.demo.notice.dto.SaveNoticeDto;
import NextLevel.demo.notice.entity.NoticeEntity;
import NextLevel.demo.notice.service.NoticeService;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/public/notice")
    public ResponseEntity<?> getAllNotice() {
        List<NoticeEntity> notices = noticeService.getAllNotice();
        List<ResponseNoticeDto> dtos = notices.stream().map(ResponseNoticeDto::of).toList();
        return ResponseEntity.ok(new SuccessResponse("success", dtos));
    }

    @PostMapping("/admin/notice")
    public ResponseEntity<?> addNotice(@ModelAttribute SaveNoticeDto dto) {
        noticeService.addNotice(dto, null);

        return ResponseEntity.ok(new SuccessResponse("success", null));
    }

    @PutMapping("/admin/notice/{id}")
    public ResponseEntity<?> updateNotice(@ModelAttribute SaveNoticeDto dto, @PathVariable("id") Long id) {
        dto.setId(id);
        noticeService.updateNotice(dto);
        if(dto.getImgs() != null && dto.getImgs().size() > 0 && dto.getImgs().get(0) != null) {
            noticeService.updateImg(dto.getId(), dto.getImgs(), null);
        }
        return ResponseEntity.ok(new SuccessResponse("success", null));
    }

    @PostMapping("/admin/notice/{id}")
    @ImgTransaction
    public ResponseEntity<?> removeNotice(@PathVariable("id") Long id) {
        noticeService.removeNotice(id, null);
        return ResponseEntity.ok(new SuccessResponse("success", null));
    }
}
