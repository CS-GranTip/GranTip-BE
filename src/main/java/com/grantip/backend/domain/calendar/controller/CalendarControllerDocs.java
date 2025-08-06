package com.grantip.backend.domain.calendar.controller;

import com.grantip.backend.domain.calendar.domain.dto.request.CalendarScholarshipRequest;
import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

public interface CalendarControllerDocs {

  @Operation(
      summary = "캘린더 조회",
      description = """
            ### 요청 파라미터
            - `startDate` (LocalDate, optional): 조회 시작일 (yyyy-MM-dd)
            - `endDate` (LocalDate, optional): 조회 종료일 (yyyy-MM-dd)
            
            ### 응답 데이터
            - `scholarshipId` (Long): 장학금 ID
            - `productName` (String): 상품명
            - `applicationStartDate` (LocalDate): 신청 시작일 (yyyy-MM-dd)
            - `applicationEndDate` (LocalDate): 신청 종료일 (yyyy-MM-dd)
            
            ### 사용 방법
            1. 클라이언트에서 `Authorization: Bearer {token}` 헤더를 포함하여 GET `/api/calendars` 요청을 보냅니다.  
            2. 쿼리 파라미터로 `startDate`, `endDate`를 yyyy-MM-dd 형식으로 전달합니다.  
            3. 서버는 해당 기간 내 장학금 일정을 조회하여 리스트 형태로 반환합니다.
            
            ### 유의 사항
            - `startDate`, `endDate`는 `yyyy-MM-dd` 형식을 준수해야 합니다.  
            - 인증된 사용자만 접근 가능합니다.
            
            ### 예외 처리
            - `UNAUTHORIZED` (401): 인증되지 않은 사용자입니다. 
            - `USER_NOT_FOUND` (404): 사용자를 찾을 수 없습니다.
            - `BAD_REQUEST` (400): 잘못된 요청입니다. (파라미터 형식 오류 등)  
            """
  )
  ResponseEntity<ApiResponse<List<CalendarScholarshipResponse>>> getCalendar(
      @AuthenticationPrincipal UserDetails userDetails,
      @ParameterObject CalendarScholarshipRequest request);
}
