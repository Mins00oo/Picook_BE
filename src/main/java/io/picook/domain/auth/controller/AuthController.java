package io.picook.domain.auth.controller;

import io.picook.domain.auth.dto.request.KakaoLoginRequest;
import io.picook.domain.auth.dto.request.LogoutRequest;
import io.picook.domain.auth.dto.request.RefreshTokenRequest;
import io.picook.domain.auth.dto.response.AuthTokenResponse;
import io.picook.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/mobile")
    public AuthTokenResponse kakaoMobileLogin(@Valid @RequestBody KakaoLoginRequest request) {
        return authService.loginWithKakao(request.accessToken());
    }

    @PostMapping("/refresh")
    public AuthTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok().build();
    }
}
