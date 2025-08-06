package com.grantip.backend.domain.scholarship.controller;

import com.grantip.backend.domain.scholarship.domain.dto.request.RecommendedScholarshipRequest;
import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ScholarshipControllerDocs {
  @Operation(
      summary = "장학금 검색 및 필터링 조회",
      description = """
            ### 요청 파라미터
            - `keyword` (String, optional): 검색어
            - `category` (ScholarshipCategory, optional): 카테고리 필터 (LOCAL, SPECIALTY, GRADE, INCOME, DISABILITY, ETC)
            - `pageNumber` (Integer, optional, default=1): 페이지 번호
            - `pageSize` (Integer, optional, default=10): 페이지 크기

            ### 응답 데이터
            - `result.content` (List of ScholarshipSummaryResponse):
              - `id` (Long): 장학금 ID
              - `productName` (String): 상품명
              - `providerName` (String): 운영 기관명
              - `productType` (ProductType): 상품 유형
              - `scholarshipCategory` (ScholarshipCategory): 장학금 분류
              - `applicationStartDate` (LocalDate): 신청 시작일 (yyyy-MM-dd)
              - `applicationEndDate` (LocalDate): 신청 종료일 (yyyy-MM-dd)
            - `result.totalElements` (Long): 전체 요소 수
            - `result.totalPages` (Integer): 전체 페이지 수
            - `result.number` (Integer): 현재 페이지 번호
            - `result.size` (Integer): 페이지 크기

            ### 사용 방법
            1. HTTP `GET /api/scholarships` 요청을 보냅니다.
            2. 필요한 쿼리 파라미터를 전달합니다.

            ### 유의 사항
            - 쿼리 파라미터는 유효성 검사를 거칩니다.
            - 인증이 필요 없습니다.

            ### 예외 처리
            - `BAD_REQUEST` (400): 파라미터 유효성 오류 발생 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<Page<ScholarshipSummaryResponse>>> searchScholarships(
      ScholarshipSearchRequest request
  );

  @Operation(
      summary = "장학금 상세 조회",
      description = """
            ### 요청 파라미터
            - `id` (Long, required): 조회할 장학금 ID

            ### 응답 데이터
            - `id` (Long): 장학금 ID
            - `productName` (String): 상품명
            - `providerName` (String): 운영 기관명
            - `providerType` (ProviderType): 운영 기관 유형
            - `productType` (ProductType): 상품 유형
            - `scholarshipCategory` (ScholarshipCategory): 분류
            - `applicationStartDate` (LocalDate): 신청 시작일
            - `applicationEndDate` (LocalDate): 신청 종료일
            - `homepageUrl` (String): 홈페이지 URL
            - `universityCategories` (List<String>)
            - `gradeCategory` (List<String>)
            - `departmentCategory` (List<String>)
            - `numOfRecipientsTotal` (Integer)
            - `recipientsByCategory` (Map<String, Integer>)
            - 기타 상세 조건 및 노트 필드

            ### 사용 방법
            1. HTTP `GET /api/scholarships/{id}` 요청을 보냅니다.

            ### 유의 사항
            - `{id}`에 해당하는 장학금이 존재해야 합니다.

            ### 예외 처리
            - `NOT_FOUND` (404): `SCHOLARSHIP_NOT_FOUND` 장학금을 찾을 수 없습니다.
            - `BAD_REQUEST` (400): PathVariable 형식 오류 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<ScholarshipDetailResponse>> findById(
      Long id
  );

  @Operation(
      summary = "추천 장학금 조회",
      description = """
            ### 요청 파라미터
            - `pageNumber` (Integer, optional, default=1): 페이지 번호
            - `pageSize` (Integer, optional, default=10): 페이지 크기

            ### 응답 데이터
            - `result.content` (List of RecommendedScholarshipResponse):
              - 기본 상세 필드 동일 (ScholarshipDetailResponse)
              - `score` (double): 추천 점수
            - 페이징 메타데이터 (totalElements, totalPages, number, size)

            ### 사용 방법
            1. HTTP `GET /api/scholarships/recommendation` 요청을 보냅니다.
            2. Header에 `Authorization: Bearer {token}`을 포함해야 합니다.

            ### 유의 사항
            - 인증된 사용자만 호출할 수 있습니다.

            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.
            - `USER_NOT_FOUND` (404): 사용자를 찾을 수 없습니다.
            - `BAD_REQUEST` (400): 파라미터 유효성 오류 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            """
  )
  ResponseEntity<ApiResponse<Page<RecommendedScholarshipResponse>>> recommend(
      UserDetails userDetails,
      RecommendedScholarshipRequest request
  );
}
