package io.picook.domain.user.controller;

import io.picook.domain.user.dto.response.MyProfileResponse;
import io.picook.domain.user.service.UserService;
import io.picook.global.common.ApiResponse;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getMyProfile_success() {
        when(userService.getMyProfile(1L))
                .thenReturn(new MyProfileResponse(1L, "picook-user", "user@kakao.com", "https://img.test/profile.png"));

        ApiResponse<MyProfileResponse> response = userController.getMyProfile(1L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getCode()).isEqualTo("S000");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().id()).isEqualTo(1L);
    }

    @Test
    void getMyProfile_fail_userNotFound() {
        when(userService.getMyProfile(999L))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        BusinessException exception = assertThrows(BusinessException.class, () -> userController.getMyProfile(999L));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
