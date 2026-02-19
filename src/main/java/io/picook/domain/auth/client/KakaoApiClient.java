package io.picook.domain.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.picook.domain.auth.config.KakaoProperties;
import io.picook.domain.auth.dto.response.KakaoUserInfo;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try {
            KakaoUserInfoResponse response = restClient.get()
                    .uri(kakaoProperties.getUserInfoUri())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(kakaoAccessToken))
                    .retrieve()
                    .body(KakaoUserInfoResponse.class);

            if (response == null || response.id() == null) {
                throw new BusinessException(ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN);
            }

            String email = response.kakaoAccount() != null ? response.kakaoAccount().email() : null;
            String nickname = null;
            String profileImage = null;
            if (response.kakaoAccount() != null && response.kakaoAccount().profile() != null) {
                nickname = response.kakaoAccount().profile().nickname();
                profileImage = response.kakaoAccount().profile().profileImageUrl();
            }

            return new KakaoUserInfo(String.valueOf(response.id()), email, nickname, profileImage);
        } catch (RestClientResponseException e) {
            log.warn("Kakao API response error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_ACCESS_TOKEN);
        } catch (RestClientException e) {
            log.error("Kakao API call failed", e);
            throw new BusinessException(ErrorCode.SOCIAL_PROVIDER_API_ERROR);
        }
    }

    private record KakaoUserInfoResponse(
            Long id,
            @JsonProperty("kakao_account")
            KakaoAccount kakaoAccount
    ) {
    }

    private record KakaoAccount(
            String email,
            Profile profile
    ) {
    }

    private record Profile(
            String nickname,
            @JsonProperty("profile_image_url")
            String profileImageUrl
    ) {
    }
}
