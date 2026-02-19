package io.picook.domain.user.dto.response;

import io.picook.domain.user.entity.User;

public record MyProfileResponse(
        Long id,
        String nickname,
        String email,
        String profileImage
) {
    public static MyProfileResponse from(User user) {
        return new MyProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage()
        );
    }
}
