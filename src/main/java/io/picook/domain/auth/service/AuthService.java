package io.picook.domain.auth.service;

import io.picook.domain.auth.client.KakaoApiClient;
import io.picook.domain.auth.dto.response.AuthTokenResponse;
import io.picook.domain.auth.dto.response.AuthUserResponse;
import io.picook.domain.auth.dto.response.KakaoUserInfo;
import io.picook.domain.auth.entity.RefreshToken;
import io.picook.domain.auth.repository.RefreshTokenRepository;
import io.picook.domain.user.entity.SocialProvider;
import io.picook.domain.user.entity.User;
import io.picook.domain.user.repository.UserRepository;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Clock clock;

    @Transactional
    public AuthTokenResponse loginWithKakao(String kakaoAccessToken) {
        KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(kakaoAccessToken);

        User user = userRepository
                .findByProviderAndProviderUserId(SocialProvider.KAKAO, kakaoUserInfo.socialId())
                .orElse(null);

        if (user == null) {
            user = User.createKakaoUser(
                    kakaoUserInfo.socialId(),
                    kakaoUserInfo.email(),
                    kakaoUserInfo.nickname(),
                    kakaoUserInfo.profileImage()
            );
            user = userRepository.save(user);
        } else {
            user.updateProfile(kakaoUserInfo.email(), kakaoUserInfo.nickname(), kakaoUserInfo.profileImage());
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthTokenResponse refresh(String refreshToken) {
        JwtTokenProvider.JwtClaims claims = jwtTokenProvider.parseRefreshToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository
                .findByTokenIdAndUser_Id(claims.tokenId(), claims.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (storedToken.isRevoked() || storedToken.isExpired(Instant.now(clock))) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        storedToken.revoke();
        return issueTokens(storedToken.getUser());
    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            JwtTokenProvider.JwtClaims claims = jwtTokenProvider.parseRefreshToken(refreshToken);
            refreshTokenRepository
                    .findByTokenIdAndUser_Id(claims.tokenId(), claims.userId())
                    .ifPresent(RefreshToken::revoke);
        } catch (BusinessException ignored) {
            // Logout is idempotent. Invalid token is treated as already logged out.
        }
    }

    private AuthTokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        JwtTokenProvider.TokenWithExpiry refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.issue(user, refreshToken.tokenId(), refreshToken.expiresAt()));
        return new AuthTokenResponse(
                accessToken,
                refreshToken.token(),
                AuthUserResponse.from(user)
        );
    }
}
