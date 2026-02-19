package io.picook.domain.auth.dto.response;

public record KakaoUserInfo(
        String socialId,
        String email,
        String nickname,
        String profileImage
) {
}
