package com.grantip.backend.domain.favorite.controller;

import com.grantip.backend.domain.favorite.domain.dto.request.FavoriteScholarshipRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface FavoriteControllerDocs {
  @Operation(
      summary = "좋아요 등록/취소",
      description = """
      ### 요청 파라미터
      - `scholarshipId` (Long, required): 좋아요를 토글할 장학금 ID
      
      ### 응답 데이터
      - `result` (Boolean):  
        - `true`: 좋아요 등록 완료  
        - `false`: 좋아요 취소 완료
      
      ### 사용 방법
      1. HTTP `POST /api/favorites/{scholarshipId}` 요청을 보냅니다.  
      2. Header에 `Authorization: Bearer {token}`을 포함해야 합니다.  
      3. 경로 변수 `{scholarshipId}`에 대상 장학금 ID를 전달합니다.
      
      ### 유의 사항
      - `{scholarshipId}`에 해당하는 장학금이 반드시 존재해야 합니다.  
      - 인증된 사용자만 호출할 수 있습니다.
      
      ### 예외 처리
      - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.  
      - `USER_NOT_FOUND` (404): 사용자를 찾을 수 없습니다.  
      - `SCHOLARSHIP_NOT_FOUND` (404): 장학금을 찾을 수 없습니다.  
      """
  )
  ResponseEntity<ApiResponse<Boolean>> toggleFavorite(
      UserDetails userDetails,
      Long scholarshipId
  );

  @Operation(
      summary = "사용자 좋아요 목록 조회",
      description = """
      ### 요청 파라미터
      - `pageNumber` (Integer, optional, default=1): 페이지 번호 (최소값 1)  
      - `pageSize` (Integer, optional, default=10): 페이지 크기 (1 이상, 최대 `PageableConstant.MAX_PAGE_SIZE`)
      
      ### 응답 데이터
      - `result.content` (List of ScholarshipSummaryResponse):  
        - `id` (Long): 장학금 ID  
        - `productName` (String): 상품명  
        - `providerName` (String): 운영 기관명  
        - `productType` (ProductType): 상품 유형  
        - `scholarshipCategory` (ScholarshipCategory): 장학금 분류  
        - `applicationStartDate` (LocalDate): 신청 시작일 (yyyy-MM-dd)  
        - `applicationEndDate` (LocalDate): 신청 종료일 (yyyy-MM-dd)  
      - `result.totalElements` (Long): 전체 요소 개수  
      - `result.totalPages` (Integer): 전체 페이지 수  
      - `result.number` (Integer): 현재 페이지 번호  
      - `result.size` (Integer): 페이지 크기
      
      ### 사용 방법
      1. HTTP `GET /api/favorites` 요청을 보냅니다.  
      2. Header에 `Authorization: Bearer {token}`을 포함해야 합니다.  
      3. 쿼리 파라미터 `pageNumber`, `pageSize`를 필요에 따라 전달합니다.
      
      ### 유의 사항
      - `pageNumber`와 `pageSize`는 유효성 검사를 거칩니다.  
      - 인증된 사용자만 호출할 수 있습니다.
      
      ### 예외 처리
      - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다.  
      - `USER_NOT_FOUND` (404): 사용자를 찾을 수 없습니다.  
      - `BAD_REQUEST` (400): 페이지 파라미터 유효성 오류 발생 시 반환됩니다.  
      """
  )
  ResponseEntity<ApiResponse<Page<ScholarshipSummaryResponse>>> getFavoritedScholarships(
      UserDetails userDetails,
      FavoriteScholarshipRequest request
  );
}
