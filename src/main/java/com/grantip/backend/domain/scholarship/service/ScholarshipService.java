package com.grantip.backend.domain.scholarship.service;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.mapper.ScholarshipMapper;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepository;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepositoryCustom;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import com.grantip.backend.global.util.database.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScholarshipService {
  private final ScholarshipRepository scholarshipRepository;
  private final ScholarshipMapper scholarshipMapper;
  private final ScholarshipRepositoryCustom scholarshipRepositoryCustom;

  // 장학금 검색 (카테고리별)
  public Page<ScholarshipSummaryResponse> searchScholarships(ScholarshipSearchRequest request){
    return scholarshipRepositoryCustom.searchScholarships(request, request.toPageable());
  }

  // 장학금 상세 조회
  public ScholarshipDetailResponse findById(Long id){
    return scholarshipMapper.toDetailResponse(scholarshipRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.SCHOLARSHIP_NOT_FOUND)));
  }
}
