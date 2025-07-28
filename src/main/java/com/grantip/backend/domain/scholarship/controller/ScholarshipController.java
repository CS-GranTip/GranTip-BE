package com.grantip.backend.domain.scholarship.controller;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.service.ScholarshipService;
import com.grantip.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scholarships")
public class ScholarshipController {
  private final ScholarshipService scholarshipService;

  // 장학금 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ScholarshipDetailResponse>> findById(@PathVariable Long id){
    return ResponseEntity.ok(
        ApiResponse.<ScholarshipDetailResponse>builder()
            .success(true)
            .code(200)
            .result(scholarshipService.findById(id))
            .message("장학금 상세 조회에 성공했습니다.")
            .build());
  }
}
