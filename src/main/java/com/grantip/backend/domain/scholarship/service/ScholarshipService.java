package com.grantip.backend.domain.scholarship.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grantip.backend.domain.scholarship.domain.constant.RecommendationStatus;
import com.grantip.backend.domain.scholarship.domain.dto.RecommendationResult;
import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipDetailResponse;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.scholarship.event.RecommendationCalculateEvent;
import com.grantip.backend.domain.scholarship.mapper.ScholarshipMapper;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepository;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepositoryCustom;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScholarshipService {
  private final ScholarshipRepository scholarshipRepository;
  private final ScholarshipMapper scholarshipMapper;
  private final ScholarshipRepositoryCustom scholarshipRepositoryCustom;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

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
  public RecommendationResult recommend(String identifier, Pageable pageable){
    String cacheKey = "recommendations::" + identifier;
    String lockKey = "recommendations:lock:" + identifier;

    // 캐시 Hit
    if(redisTemplate.hasKey(cacheKey)){
      Object cachedObject = redisTemplate.opsForValue().get(cacheKey);

      List<RecommendedScholarshipResponse> fullList = objectMapper.convertValue(
          cachedObject,
          new TypeReference<>() {
          }
      );
      Page<RecommendedScholarshipResponse> pagedData = createPage(fullList, pageable);

      return new RecommendationResult(
          pagedData.isEmpty() ? RecommendationStatus.EMPTY : RecommendationStatus.COMPLETED,
          pagedData
          );
    }

    // 캐시 Miss & Lock (계산 중)
    if(redisTemplate.hasKey(lockKey)){
      return new RecommendationResult(RecommendationStatus.PENDING, null);
    }

    // 캐시 Miss & No Lock -> 비동기 계산 트리거
    redisTemplate.opsForValue().set(lockKey, "locked", Duration.ofSeconds(30));
    applicationEventPublisher.publishEvent(new RecommendationCalculateEvent(identifier));

    return new RecommendationResult(RecommendationStatus.PENDING, null);
  }

  // List를 Page로 변환하는 헬퍼 메서드
  private Page<RecommendedScholarshipResponse> createPage(List<RecommendedScholarshipResponse> fullList, Pageable pageable) {
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), fullList.size());
    List<RecommendedScholarshipResponse> pagedList = (start >= fullList.size()) ? List.of() : fullList.subList(start, end);
    return new PageImpl<>(pagedList, pageable, fullList.size());
  }
}
