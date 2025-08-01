package com.grantip.backend.domain.scholarship.service;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.scholarship.mapper.ScholarshipMapper;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepository;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepositoryCustom;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScholarshipService {
  private final ScholarshipRepository scholarshipRepository;
  private final ScholarshipMapper scholarshipMapper;
  private final ScholarshipRepositoryCustom scholarshipRepositoryCustom;
  private final ScholarshipRecommendationService recommendationService;

  /**
   * 장학금 Id로 조회
   */
  @Transactional(readOnly = true)
  public Scholarship findById(Long id){
    return scholarshipRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.SCHOLARSHIP_NOT_FOUND));
  }

  /**
   * 장학금 검색 + 카테고리별 필터링 메서드
   */
  @Transactional(readOnly = true)
  public Page<ScholarshipSummaryResponse> searchScholarships(ScholarshipSearchRequest request){
    return scholarshipRepositoryCustom.searchScholarships(request, request.toPageable());
  }

  /**
   * 장학금 상세 조회 메서드
   */
  @Transactional(readOnly = true)
  public ScholarshipDetailResponse findDetailedScholarship(Long id){
    return scholarshipMapper.toDetailResponse(scholarshipRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.SCHOLARSHIP_NOT_FOUND)));
  }

  /**
   * 사용자별 추천 장학금 조회 메서드
   */
  @Transactional(readOnly = true)
  public Page<RecommendedScholarshipResponse> recommend(String identifier, Pageable pageable){
    // 전체 추천 목록 조회 (Cache Hit이면 Redis에서, Cache Miss이면 계산 후 Redis에 저장하고 결과를 가져옴)
    List<RecommendedScholarshipResponse> fullList = recommendationService.calculateRecommendations(identifier);

    // 페이징 처리
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), fullList.size());
    List<RecommendedScholarshipResponse> pagedList = (start >= fullList.size()) ? List.of() : fullList.subList(start, end);

    return new PageImpl<>(pagedList, pageable, fullList.size());
  }
}
