package io.picook.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.picook.domain.auth.service.JwtTokenProvider;
import io.picook.domain.auth.service.TokenType;
import io.picook.domain.user.dto.response.MyProfileResponse;
import io.picook.domain.user.service.UserService;
import io.picook.global.error.ErrorCode;
import io.picook.global.error.GlobalExceptionHandler;
import io.picook.global.error.exception.BusinessException;
import io.picook.global.security.JwtAuthenticationFilter;
import io.picook.global.security.RestAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserApiIntegrationTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(new ObjectMapper().findAndRegisterModules());
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, entryPoint);
        OncePerRequestFilter requireAuthenticationFilter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    entryPoint.commence(request, response, new BadCredentialsException("Unauthorized"));
                    return;
                }
                filterChain.doFilter(request, response);
            }
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .addFilters(jwtAuthenticationFilter, requireAuthenticationFilter)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyProfile_http_success() throws Exception {
        when(jwtTokenProvider.parseAccessToken(anyString()))
                .thenReturn(new JwtTokenProvider.JwtClaims(1L, "token-1", TokenType.ACCESS));
        when(userService.getMyProfile(1L))
                .thenReturn(new MyProfileResponse(1L, "picook-user", "user@kakao.com", "https://img.test/profile.png"));

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickname").value("picook-user"));
    }

    @Test
    void getMyProfile_http_fail_invalidToken() throws Exception {
        when(jwtTokenProvider.parseAccessToken(anyString()))
                .thenThrow(new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN));

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer bad-token"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("U005"));
    }

    @Test
    void getMyProfile_http_fail_withoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("U005"));
    }

    @Test
    void getMyProfile_http_fail_userNotFound() throws Exception {
        when(jwtTokenProvider.parseAccessToken(anyString()))
                .thenReturn(new JwtTokenProvider.JwtClaims(999L, "token-2", TokenType.ACCESS));
        when(userService.getMyProfile(999L))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-missing-user"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("U006"));
    }
}
