package io.picook.domain.user.dto.response;

public record MyProfileResponse(
        Long id,
        String nickname,
        String email,
        String profileImage
) {
}
