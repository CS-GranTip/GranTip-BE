package com.grantip.backend.domain.user.controller;

import com.grantip.backend.domain.user.domain.dto.request.UpdateRequest;
import com.grantip.backend.domain.user.domain.dto.request.VerifyPassword;
import com.grantip.backend.domain.user.domain.dto.response.MyPageResponse;
import com.grantip.backend.domain.user.domain.dto.response.UserResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserControllerDocs {
  @Operation(
      summary = "마이페이지 정보 조회",
      description = """
            ### 요청 파라미터
            - 인증 헤더: `Authorization: Bearer {token}` (required)

            ### 응답 데이터
            - `userId` (Long): 사용자 ID
            - `username` (String): 사용자 이름
            - `userUniversity` (String): 소속 대학명

            ### 사용 방법
            1. HTTP `GET /user/mypage` 요청을 보냅니다.
            2. 헤더에 유효한 AccessToken을 포함합니다.

            ### 유의 사항
            - 인증된 사용자만 접근 가능합니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `USER_NOT_FOUND` (404): 해당 사용자를 찾을 수 없습니다.
            """
  )
  ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(
      UserDetails userDetails
  );

  @Operation(
      summary = "회원정보 수정",
      description = """
            ### 요청 파라미터
            - 인증 헤더: `Authorization: Bearer {token}` (required)
            - 요청 본문 (UpdateRequest):
              - `phone` (String, required)
              - `universityCategoryId` (Long, required)
              - `currentSchool` (String, required)
              - `highSchool` (String, required)
              - `universityYear` (UnivYear, required)
              - `gender` (Gender, required)
              - `addressId` (Long, required)
              - `residentAddressId` (Long, required)
              - `qualificationCodes` (Set<QualificationCode>, optional)
              - 학업 성적 및 지원 정보 필드 (optional)

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `POST /user/update` 요청을 보냅니다.
            2. 헤더에 유효한 AccessToken을 포함합니다.
            3. 요청 본문에 수정할 사용자 정보를 JSON으로 전달합니다.

            ### 유의 사항
            - 필수 필드는 누락 시 에러가 발생합니다.
            - 일부 필드는 null 허용 여부에 따라 처리됩니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `BAD_REQUEST` (400): 요청 데이터 유효성 오류 발생 시 반환됩니다.
            - `USER_NOT_FOUND` (404): 해당 사용자를 찾을 수 없습니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> updateInfo(
      UserDetails userDetails,
      UpdateRequest request
  );

  @Operation(
      summary = "회원정보 조회",
      description = """
            ### 요청 파라미터
            - 인증 헤더: `Authorization: Bearer {token}` (required)

            ### 응답 데이터
            - `phone` (String)
            - `currentSchool` (String)
            - `highSchool` (String)
            - `universityYear` (UnivYear)
            - `gender` (Gender)
            - `address` (RegionDto)
            - `residentAddress` (RegionDto)
            - `qualificationCodes` (Set<QualificationCode>)
            - 학업 성적 및 지원 정보 필드

            ### 사용 방법
            1. HTTP `GET /user` 요청을 보냅니다.
            2. 헤더에 유효한 AccessToken을 포함합니다.

            ### 유의 사항
            - 인증된 사용자만 접근 가능합니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `USER_NOT_FOUND` (404): 해당 사용자를 찾을 수 없습니다.
            """
  )
  ResponseEntity<ApiResponse<UserResponse>> userInfo(
      UserDetails userDetails
  );

  @Operation(
      summary = "회원 탈퇴",
      description = """
            ### 요청 파라미터
            - 인증 헤더: `Authorization: Bearer {token}` (required)

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `DELETE /user` 요청을 보냅니다.
            2. 헤더에 유효한 AccessToken을 포함합니다.

            ### 유의 사항
            - 삭제 시 관련 데이터가 영구 삭제될 수 있습니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `USER_NOT_FOUND` (404): 해당 사용자를 찾을 수 없습니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> deleteUser(
      UserDetails userDetails
  );

  @Operation(
      summary = "비밀번호 검증",
      description = """
            ### 요청 파라미터
            - 인증 헤더: `Authorization: Bearer {token}` (required)
            - 요청 본문 (VerifyPassword):
              - `password1` (String, required): 새 비밀번호
              - `password2` (String, required): 새 비밀번호 확인

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `PATCH /user/password/verify` 요청을 보냅니다.
            2. 헤더에 유효한 AccessToken을 포함합니다.
            3. 요청 본문에 새 비밀번호와 확인 값을 전달합니다.

            ### 유의 사항
            - `password1`과 `password2`가 일치해야 합니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `BAD_REQUEST` (400): 비밀번호 형식 또는 불일치 시 반환됩니다.
            - `USER_NOT_FOUND` (404): 해당 사용자를 찾을 수 없습니다.
            """
  )
  ResponseEntity<ApiResponse<Void>> verifyPassword(
      UserDetails userDetails,
      VerifyPassword verifyPassword
  );
}
