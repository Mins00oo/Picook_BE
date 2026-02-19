package io.picook.global.config;

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
@ConfigurationProperties(prefix = "infra.http-client")
public class InfraHttpClientProperties {

    @Positive
    private int connectTimeoutMs;

    @Positive
    private int readTimeoutMs;
}
