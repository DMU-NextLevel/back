package NextLevel.demo.message;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.user.entity.UserEntity;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoMessageService {

    private final KakaoTokenRepository kakaoTokenRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoSecret;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String KAKAO_REFRESH_URL;

    private String KAKAO_ACCESS_URL = "https://kauth.kakao.com";

    public static final String HELLO = "hello";

    public void sendMessage(UserEntity to, String message) {
        KakaoTokenEntity token = kakaoTokenRepository.findById(1L).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_KAKAO_REFRESH_TOKEN)
        );

        try {
            sendMessage(to, message, token.getAccess());
        } catch (Exception e) {
            e.printStackTrace();
            token = updateAccessToken(token.getRefresh());
            sendMessage(to, message, token.getAccess());
        }
    }

    private void sendMessage(UserEntity to, String message, String access){
        Map<String, Object> response = WebClient.create("https://kapi.kakao.com/v2/api/talk/memo/send")
            .post()
            .body(
                BodyInserters
                    .fromFormData("message_type", "AT")
                    .with("sender_key", kakaoId)
                    .with("cid", to.getId().toString())
                    .with("template_id", "120500")
                    .with("phone_number", to.getNumber())
                    .with("sender_no", to.getNumber())
                    .with("message", message)
            )
            .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + access)
            .header(HttpHeaders.ACCEPT, "*/*")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .bodyToMono(Map.class)
            .block();
    }

    public void saveRefreshToken(String code) {
        Map<String, String> response = WebClient.create(KAKAO_REFRESH_URL).post()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", kakaoId)
                .queryParam("client_secret", kakaoSecret)
                .queryParam("code", code)
                .build(true)
            )
            .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .bodyToMono(Map.class)
            .block();

        kakaoTokenRepository.save(KakaoTokenEntity.builder()
            .refresh(response.get("refresh_token").toString())
            .access(response.get("access_token").toString())
            .build());
    }

    private KakaoTokenEntity updateAccessToken(String refreshToken) {
        Map<String, Object> response = WebClient.create(KAKAO_ACCESS_URL).post()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .path("/oauth/token")
                .build(true)
            )
            .body(
                BodyInserters.fromFormData("grant_type", "refresh_token")
                    .with("client_id", kakaoId)
                    .with("client_secret", kakaoSecret)
                    .with("refresh_token", refreshToken)
            )
            .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(s -> Mono.error(new RuntimeException(s))))
            .bodyToMono(Map.class)
            .block();

        return kakaoTokenRepository.save(KakaoTokenEntity.builder()
            .refresh(response.get("refresh_token") == null ? refreshToken : response.get("refresh_token").toString())
            .access(response.get("access_token").toString())
            .build());
    }

}
