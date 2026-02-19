package io.picook.domain.user.service;

import io.picook.domain.user.dto.response.MyProfileResponse;
import io.picook.domain.user.entity.User;
import io.picook.domain.user.repository.UserRepository;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getMyProfile_success() {
        User user = User.createKakaoUser("kakao-123", "user@kakao.com", "picook-user", "https://img.test/profile.png");
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MyProfileResponse result = userService.getMyProfile(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nickname()).isEqualTo("picook-user");
        assertThat(result.email()).isEqualTo("user@kakao.com");
        assertThat(result.profileImage()).isEqualTo("https://img.test/profile.png");
    }

    @Test
    void getMyProfile_fail_userNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.getMyProfile(999L));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
