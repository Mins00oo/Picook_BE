package io.picook.domain.auth.dto.response;

import io.picook.domain.user.entity.User;

public record AuthUserResponse(
        Long id,
        String nickname,
        String email,
        String profileImage
) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage()
        );
    }
}
