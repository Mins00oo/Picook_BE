package io.picook.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final InfraHttpClientProperties infraHttpClientProperties;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(infraHttpClientProperties.getConnectTimeoutMs());
        requestFactory.setReadTimeout(infraHttpClientProperties.getReadTimeoutMs());
        return requestFactory;
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder, ClientHttpRequestFactory clientHttpRequestFactory) {
        return builder
                .requestFactory(clientHttpRequestFactory)
                .build();
    }
}
