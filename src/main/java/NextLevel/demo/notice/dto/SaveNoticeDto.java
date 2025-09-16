package NextLevel.demo.notice.dto;

import NextLevel.demo.notice.entity.NoticeEntity;
import NextLevel.demo.notice.entity.NoticeImgEntity;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Setter @Getter
public class SaveNoticeDto {

    private static Pattern compiledPattern = Pattern.compile("src=\\\".*?\\\"");

    private Long id;

    @NotNull
    private String title;
    private String content;

    private List<MultipartFile> imgs;

    private List<NoticeImgEntity> imgEntities;

    public NoticeEntity toEntity() {
        return NoticeEntity
            .builder()
            .id(id)
            .title(title)
            .content(content)
            .build();
    }

    public void setContent(String content) {
        Matcher matcher = compiledPattern.matcher(content);

        // 결과를 담을 StringBuffer
        StringBuffer result = new StringBuffer();

        // 각 src="..." 부분을 src=""로 대체
        while (matcher.find()) {
            matcher.appendReplacement(result, "src=");
        }
        matcher.appendTail(result);

        this.content = result.toString();
    }
}
