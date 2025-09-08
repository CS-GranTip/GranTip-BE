package com.grantip.backend.domain.user.controller;

import com.grantip.backend.domain.user.domain.dto.CustomUserDetails;
import com.grantip.backend.domain.user.domain.dto.request.LoginRequest;
import com.grantip.backend.domain.user.domain.dto.request.SignupRequest;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {
  @Operation(
      summary = "회원가입",
      description = """
            ### 요청 파라미터
            - `email` (String, required): 이메일 (유효한 이메일 형식)
            - `password` (String, required): 비밀번호
            - `username` (String, required): 사용자 이름
            - `role` (Role, optional): 사용자 역할 (예: USER)
            - `phone` (String, required): 휴대폰 번호
            - `universityCategoryId` (Long, required): 대학 구분 ID
            - `currentSchool` (String, required): 재학 중인 학교명
            - `highSchool` (String, required): 졸업(재학) 고등학교명
            - `universityYear` (UnivYear, optional): 대학 학년 (예: SEVENTH_SEMESTER)
            - `gender` (Gender, optional): 성별 (MALE, FEMALE)
            - `addressId` (Long, required): 주소 ID
            - `residentAddressId` (Long, required): 주민등록 주소 ID
            - `available` (boolean, optional): 활성 상태 (default: true)

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `POST /auth/signup` 요청을 보냅니다.
            2. 요청 본문에 SignupRequest JSON을 포함합니다.
            3. 성공 시 HTTP 201 Created 상태와 성공 메시지를 반환합니다.

            ### 유의 사항
            - 모든 `required` 필드는 유효성을 검사합니다.
            - `email` 중복 시 에러가 발생합니다.

            ### 예외 처리
            - `DUPLICATE_LOGIN_ID` (400): 이미 존재하는 아이디입니다.
            - `BAD_REQUEST` (400): 유효성 검사 실패 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> signup(
      SignupRequest request
  );

  @Operation(
      summary = "로그인",
      description = """
          ### 기능 요약
          - 사용자 인증을 처리하고, 접근 토큰(Access Token)과 갱신 토큰(Refresh Token)을 발급합니다.
          - **또한, 로그인 성공 시 사용자의 추천 캐시가 없는 경우, 백그라운드에서 개인별 맞춤 장학금 추천 목록 계산을 비동기적으로 시작합니다.**
          - 이 비동기 작업은 로그인 응답 시간에 영향을 주지 않으며, 이후 추천 조회 API의 응답 속도를 향상시킵니다.
          
          ### 요청 파라미터
          - `email` (String, required): 이메일
          - `password` (String, required): 비밀번호

          ### 응답 데이터
          - 없음 (Void)
          
          ### 사용 방법
          1. HTTP `POST /auth/login` 요청을 보냅니다.
          2. 요청 본문에 LoginRequest JSON을 포함합니다.
          3. 성공 시 응답 본문에 토큰 정보를 반환합니다.

          ### 유의 사항
          - **추천 계산 트리거:** 이 API는 성공적으로 로그인한 사용자의 추천 캐시가 없을 경우, 추천 목록 생성을 위한 백그라운드 작업을 트리거합니다. 
          - 이후 추천 장학금 조회 API 호출 시, 이 계산이 진행 중이라면 `202 Accepted` 상태 코드가 반환될 수 있습니다.

          ### 예외 처리
          - `INVALID_CREDENTIALS` (401): 아이디 또는 비밀번호가 올바르지 않습니다.
          - `BAD_REQUEST` (400): 요청 형식 오류 시 반환됩니다.
          - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
          """
  )
  ResponseEntity<ApiResponse<Void>> login(
      LoginRequest request
  );

  @Operation(
      summary = "토큰 재발급",
      description = """
            ### 요청 파라미터
            - HttpOnly 쿠키 `refreshToken` (required)

            ### 응답 데이터
            - 없음 (Void) (새로운 AccessToken은 `Authorization` 헤더, 새로운 RefreshToken은 HttpOnly 쿠키에 설정)

            ### 사용 방법
            1. HTTP `POST /auth/reissue` 요청을 보냅니다.
            2. 요청 시 기존 RefreshToken 쿠키를 자동 전송합니다.
            3. 성공 시 새로운 토큰을 헤더와 쿠키로 반환합니다.

            ### 유의 사항
            - 만료된 또는 유효하지 않은 RefreshToken은 재발급에 실패합니다.

            ### 예외 처리
            - `INVALID_REFRESH_TOKEN` (401): 유효하지 않은 리프레시 토큰입니다.
            - `TOKEN_EXPIRED` (401): 토큰이 만료되었습니다.
            - `BAD_REQUEST` (400): RefreshToken 누락 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request);

  @Operation(
      summary = "로그아웃",
      description = """
            ### 요청 파라미터
            - `Authorization` 헤더: `Bearer {accessToken}` (required)

            ### 응답 데이터
            - 없음 (Void) (RefreshToken 쿠키 삭제)

            ### 사용 방법
            1. HTTP `POST /auth/logout` 요청을 보냅니다.
            2. `Authorization` 헤더를 포함해야 합니다.
            3. 성공 시 RefreshToken 쿠키가 만료 처리됩니다.

            ### 유의 사항
            - RefreshToken이 만료되거나 누락된 경우에도 로그아웃을 수행합니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> logout(
      CustomUserDetails userDetails,
      HttpServletRequest request
  );
}
