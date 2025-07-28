package com.grantip.backend.domain.scholarship.domain.dto.response;

import com.grantip.backend.domain.scholarship.domain.constant.ProductType;
import com.grantip.backend.domain.scholarship.domain.constant.ScholarshipCategory;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipSummaryResponse {
  private Long id;

  private String productName;

  private String providerName;

  private ProductType productType;

  private ScholarshipCategory scholarshipCategory;

  private LocalDate applicationStartDate;

  private LocalDate applicationEndDate;
}
