package io.picook.domain.user.service;

import io.picook.domain.user.dto.response.MyProfileResponse;
import io.picook.domain.user.entity.User;
import io.picook.domain.user.repository.UserRepository;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new MyProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage()
        );
    }
}
