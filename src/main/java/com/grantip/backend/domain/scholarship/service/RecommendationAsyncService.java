package com.grantip.backend.domain.scholarship.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationAsyncService {
  private final ScholarshipRecommendationService recommendationService;

  @Async
  public void triggerRecommendationCalculation(String identifier){
    recommendationService.calculateRecommendations(identifier);
  }
}
