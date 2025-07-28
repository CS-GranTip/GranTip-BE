package com.grantip.backend.domain.scholarship.domain.dto.response;

import com.grantip.backend.domain.scholarship.domain.constant.ProductType;
import com.grantip.backend.domain.scholarship.domain.constant.ProviderType;
import com.grantip.backend.domain.scholarship.domain.constant.ScholarshipCategory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ScholarshipDetailResponse {
  private Long id;

  private String productName;

  private String providerName;

  private ProviderType providerType;

  private ProductType productType;

  private ScholarshipCategory scholarshipCategory;

  private LocalDate applicationStartDate;

  private LocalDate applicationEndDate;

  private String homepageUrl;

  private List<String> universityCategories = new ArrayList<>();

  private List<String> gradeCategory;

  private List<String> departmentCategory;

  private Integer numOfRecipientsTotal;

  private Map<String, Integer> recipientsByCategory;

  private boolean isRecommendationRequired;

  private boolean isDuplicateSupportRestricted;

  private List<String> gradeCriteriaDetail;

  private List<String> incomeCriteriaDetail;

  private List<String> supportDetail;

  private List<String> specificQualificationDetail;

  private List<String> regionResidenceDetail;

  private List<String> selectionMethodDetail;

  private List<String> selectionPersonnelDetail;

  private List<String> qualificationRestrictionDetail;

  private List<String> recommendationNeededDetail;

  private List<String> requiredDocumentsDetail;

  private String gradeCriteriaNotes;

  private String incomeCriteriaNotes;

  private String supportNotes;

  private String specificQualificationNotes;

  private String regionResidenceNotes;

  private String selectionMethodNotes;

  private String selectionPersonnelNotes;

  private String qualificationRestrictionNotes;

  private String recommendationNeededNotes;

  private String requiredDocumentsNotes;
}
