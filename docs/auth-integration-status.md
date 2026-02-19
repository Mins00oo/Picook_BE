# 인증 연동 현황

## 구현 완료 API

1. `POST /api/auth/kakao/mobile`
2. `POST /api/auth/refresh`
3. `POST /api/auth/logout`

위 3개 API는 프론트에서 전달한 명세의 요청/응답 형식에 맞게 구현되어 있습니다.

## 서버 동작 구현 범위

1. `https://kapi.kakao.com/v2/user/me` 호출로 카카오 access token 검증
2. `(provider, providerUserId)` 기준 사용자 조회 후 생성/갱신(upsert)
3. 백엔드 JWT 발급 (`accessToken`, `refreshToken`)
4. refresh token DB 저장 (`refresh_tokens` 테이블)
5. `POST /api/auth/refresh` 호출 시 refresh token 회전(기존 토큰 폐기 후 신규 발급)
6. `POST /api/auth/logout` 호출 시 refresh token 폐기 처리 (멱등성 보장)
7. Spring Security 적용으로 `/api/auth/**` 외 API 인증 보호

## 현재 남은 작업

1. `GET /api/users/me`에서는 `SecurityContext` 인증 주체(`userId`) 연결을 완료했습니다.
2. 나머지 비즈니스 API도 동일한 방식으로 `userId`를 도메인 로직에 연결해야 합니다.
3. 향후 역할 기반 인가가 필요하면 권한 모델(ROLE)과 매핑 정책을 추가해야 합니다.

## 권장 다음 단계

1. 보호 API 컨트롤러/서비스에서 `userId` 기반 사용자 식별 적용
2. 권한 정책이 필요한 엔드포인트부터 단계적으로 ROLE 인가 규칙 추가
