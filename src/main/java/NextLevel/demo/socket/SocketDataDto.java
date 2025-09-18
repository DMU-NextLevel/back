package NextLevel.demo.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class SocketDataDto {
    private String text;
    private LocalDateTime createdAt;

    public SocketDataDto(String text, LocalDateTime createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        // JSON.parse('{"test": "test"}');
        return "'{\"text\": \""+text+"\", \"createdAt\": \""+createdAt+"\"}'";
    }
}
