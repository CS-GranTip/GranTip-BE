package com.grantip.backend.domain.scholarship.repository;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScholarshipRepositoryCustom {
  Page<ScholarshipSummaryResponse> searchScholarships(ScholarshipSearchRequest request, Pageable pageable);
}
