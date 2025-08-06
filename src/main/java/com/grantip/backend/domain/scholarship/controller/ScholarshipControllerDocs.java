package com.grantip.backend.domain.scholarship.controller;

import com.grantip.backend.domain.scholarship.domain.dto.request.RecommendedScholarshipRequest;
import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
      summary = "개인별 맞춤 장학금 추천 조회",
      description = """
        개인화된 추천 장학금 목록을 비동기적으로 조회합니다.
        
        ### 기능 요약
        - 사용자의 정보를 바탕으로 추천 점수가 계산된 장학금 목록을 제공합니다.
        - **사용자가 로그인할 때 추천 캐시가 없는 경우, 백그라운드에서 비동기적으로 추천 계산을 시작합니다.**
        - 이 API는 계산이 진행 중이거나 완료된 추천 결과를 조회하는 데 사용됩니다. 클라이언트는 서버의 응답 상태에 따라 데이터를 즉시 받거나, 잠시 후 다시 요청(폴링)해야 할 수 있습니다.

        ### 요청 흐름
        1. **최초 요청:** 로그인 후, 클라이언트는 `GET /api/scholarships/recommendation`을 호출합니다.
        2. **서버 응답 분기:**
           - **(A) 데이터 즉시 반환 (`200 OK`):** 캐시에 이미 추천 결과가 있는 경우, 즉시 데이터와 함께 응답합니다.
           - **(B) 처리 시작 또는 진행 중 알림 (`202 Accepted`):** 캐시에 결과가 없어 **로그인 시 시작된 계산이 아직 진행 중**인 경우, `result`가 `null`인 응답과 함께 "처리 중"임을 알립니다. 서버에서는 **Redis Lock을 통해 중복 계산을 방지**하므로, 여러 번 호출해도 계산은 한 번만 실행됩니다.
        3. **클라이언트 후속 조치:**
           - **(A)의 경우:** 받은 데이터를 화면에 표시합니다.
           - **(B)의 경우:** 로딩 UI를 표시하고, **1~2초 간격 (권장: 1.5초)으로** 동일한 API를 다시 호출(폴링)하여 데이터가 준비되었는지 확인합니다. 최초 계산에 **통상 3~4초가 소요**되므로, 2~3회 폴링 내에 `200 OK` 응답을 기대할 수 있습니다.

        ### 요청 파라미터
        - `page` (Integer, optional, default=0): 페이지 번호 (0부터 시작)
        - `size` (Integer, optional, default=10): 페이지 크기
        
        ### 응답 데이터
        - **상태 200 (OK)**
        ```json
        {
          "success": true,
          "code": 200,
          "result": {
            "content": [
              {
                // RecommendedScholarshipResponse의 기본 필드 (장학금 ID, 명칭 등)
                // + `score` (double): 추천 점수
              }
            ],
            "pageable": {
              "pageNumber": 0,
              "pageSize": 10
            },
            "totalElements": 15,
            "totalPages": 2,
            "number": 0,
            "size": 10
          },
          "message": "추천 장학금 조회에 성공했습니다."
        }
        ```
        - **상태 202 (Accepted)**
        ```json
        {
          "success": true,
          "code": 202,
          "result": null,
          "message": "추천 정보를 준비하고 있습니다. 잠시 후 다시 시도해주세요."
         }
        ```
        - **상태 200 (OK, 빈 결과)**
        ```json
        {
          "success": true,
          "code": 200,
          "result": {
            "content": [],
            "pageable": { "pageNumber": 0, "pageSize": 10 },
            "totalElements": 0,
            "totalPages": 0,
            "number": 0,
            "size": 10
          },
          "message": "추천할 수 있는 장학금이 없습니다."
        }
        ```
        
        ### 예외 처리
        - `401 UNAUTHORIZED`: 인증 정보가 없거나 토큰이 유효하지 않습니다.
        - `404 USER_NOT_FOUND`: 해당 이메일의 사용자를 찾을 수 없습니다.
        - `400 BAD_REQUEST`: `page` 또는 `size` 파라미터 유효성 검사 실패 시 발생합니다.
        - `500 INTERNAL_SERVER_ERROR`: 서버 내부 오류 발생 시 반환됩니다.
        """
  )
  ResponseEntity<ApiResponse<Page<RecommendedScholarshipResponse>>> recommend(
      UserDetails userDetails,
      RecommendedScholarshipRequest request
  );
}
