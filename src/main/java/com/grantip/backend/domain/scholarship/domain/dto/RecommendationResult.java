package com.grantip.backend.domain.scholarship.domain.dto;

import com.grantip.backend.domain.scholarship.domain.constant.RecommendationStatus;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import org.springframework.data.domain.Page;


public record RecommendationResult(RecommendationStatus status, Page<RecommendedScholarshipResponse> data) {
}
