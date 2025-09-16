package NextLevel.demo.notice.dto;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.notice.entity.NoticeEntity;
import NextLevel.demo.notice.entity.NoticeImgEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter @Getter
public class ResponseNoticeDto {

    private static Pattern compiledImgPattern = Pattern.compile("src=");

    private Long id;

    private String title;
    private String content;
    private LocalDateTime createdAt;

    private List<String> imgs;

    public static ResponseNoticeDto of(NoticeEntity entity) {
        ResponseNoticeDto dto = new ResponseNoticeDto();
        dto.id = entity.getId();
        dto.title = entity.getTitle();
        dto.content = makeContent(entity.getContent(), entity.getImgs().stream().map(NoticeImgEntity::getImg).map(ImgEntity::getUri).toList());
        dto.createdAt = entity.getCreatedAt();
        dto.imgs = entity.getImgs().stream().map(NoticeImgEntity::getImg).map(ImgEntity::getUri).toList();
        return dto;
    }

    private static String makeContent(String content, List<String> imgs) {
        Matcher matcher = compiledImgPattern.matcher(content);
        StringBuffer result = new StringBuffer();
        int index = 0;

        while (matcher.find() && index < imgs.size()) {
            String replacement = "src='https://api.nextlevel.r-e.kr/img/" + imgs.get(index++) + "'";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

}
