package io.picook.global.security;

import io.picook.domain.auth.service.JwtTokenProvider;
import io.picook.domain.auth.service.TokenType;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.exception.BusinessException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_success_validAccessToken() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, restAuthenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtTokenProvider.parseAccessToken("valid-token"))
                .thenReturn(new JwtTokenProvider.JwtClaims(1L, "token-id", TokenType.ACCESS));

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(1L);
        verify(restAuthenticationEntryPoint, never()).commence(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }

    @Test
    void doFilterInternal_fail_invalidAccessToken() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, restAuthenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtTokenProvider.parseAccessToken("bad-token"))
                .thenThrow(new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN));

        filter.doFilter(request, response, chain);

        verify(restAuthenticationEntryPoint).commence(
                ArgumentMatchers.eq(request),
                ArgumentMatchers.eq(response),
                ArgumentMatchers.any()
        );
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
