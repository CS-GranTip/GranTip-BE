package com.grantip.backend.domain.scholarship.repository;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScholarshipRepositoryCustom {
  Page<ScholarshipSummaryResponse> searchScholarships(ScholarshipSearchRequest request, Pageable pageable);

  List<Scholarship> findFilteredScholarships(User user, UserExtraInfo userInfo);
}
