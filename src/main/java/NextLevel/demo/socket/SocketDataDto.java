package NextLevel.demo.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class SocketDataDto {
    private String data;
    private LocalDateTime createdAt;

    public SocketDataDto(String data, LocalDateTime createdAt) {
        this.data = data;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        // JSON.parse('{"test": "test"}');
        return "{\"data\": \""+ data +"\", \"createdAt\": \""+createdAt+"\"}";
    }
}
