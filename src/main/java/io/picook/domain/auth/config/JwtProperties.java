package io.picook.domain.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @Positive
    private long accessTokenExpirationSeconds;

    @Positive
    private long refreshTokenExpirationSeconds;
}
