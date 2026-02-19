package io.picook.domain.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.picook.domain.auth.config.JwtProperties;
import io.picook.domain.user.entity.User;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final Clock clock;
    private SecretKey signingKey;

    @PostConstruct
    void init() {
        byte[] secretBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new BusinessException(ErrorCode.JWT_CONFIGURATION_INVALID);
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.getAccessTokenExpirationSeconds(), TokenType.ACCESS, UUID.randomUUID().toString());
    }

    public TokenWithExpiry generateRefreshToken(User user) {
        String tokenId = UUID.randomUUID().toString();
        Instant now = Instant.now(clock);
        return new TokenWithExpiry(
                generateToken(user, jwtProperties.getRefreshTokenExpirationSeconds(), TokenType.REFRESH, tokenId),
                tokenId,
                now.plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds())
        );
    }

    public JwtClaims parseRefreshToken(String token) {
        return parseToken(token, TokenType.REFRESH, ErrorCode.INVALID_REFRESH_TOKEN);
    }

    public JwtClaims parseAccessToken(String token) {
        return parseToken(token, TokenType.ACCESS, ErrorCode.INVALID_ACCESS_TOKEN);
    }

    private JwtClaims parseToken(String token, TokenType expectedType, ErrorCode invalidTokenErrorCode) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String tokenId = claims.getId();
            String type = claims.get("type", String.class);

            if (tokenId == null || type == null || !expectedType.name().equals(type)) {
                throw new BusinessException(invalidTokenErrorCode);
            }
            return new JwtClaims(userId, tokenId, TokenType.valueOf(type));
        } catch (BusinessException e) {
            throw e;
        } catch (IllegalArgumentException | JwtException e) {
            throw new BusinessException(invalidTokenErrorCode);
        }
    }

    private String generateToken(User user, long expiresInSeconds, TokenType tokenType, String tokenId) {
        Instant now = Instant.now(clock);
        Instant expiration = now.plusSeconds(expiresInSeconds);
        return Jwts.builder()
                .id(tokenId)
                .subject(String.valueOf(user.getId()))
                .claim("type", tokenType.name())
                .claim("provider", user.getProvider().name())
                .claim("providerUserId", user.getProviderUserId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public record JwtClaims(
            Long userId,
            String tokenId,
            TokenType tokenType
    ) {
    }

    public record TokenWithExpiry(
            String token,
            String tokenId,
            Instant expiresAt
    ) {
    }
}
