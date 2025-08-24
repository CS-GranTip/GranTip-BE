package com.grantip.backend.domain.scholarship.event;

import com.grantip.backend.domain.scholarship.service.ScholarshipRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEventListener {
  private final ScholarshipRecommendationService recommendationService;

  @Async
  @EventListener
  public void handleCalculationEvent(RecommendationCalculateEvent event) {
    log.debug("추천 계산 이벤트 수신. 캐시가 없는 경우 계산을 시작합니다. identifier: {}", event.identifier());
    recommendationService.calculateRecommendations(event.identifier());
  }

  @Async
  @EventListener
  public void handleRefreshEvent(RecommendationRefreshEvent event) {
    log.debug("추천 갱신 이벤트 수신. 캐시 삭제 후 재계산을 시작합니다. identifier: {}", event.identifier());
    recommendationService.evict(event.identifier());
    recommendationService.calculateRecommendations(event.identifier());
  }
}
